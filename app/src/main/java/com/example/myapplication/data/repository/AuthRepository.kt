package com.example.myapplication.data.repository

import com.example.myapplication.data.local.AuthStorage
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.model.LoginRequest
import com.example.myapplication.data.remote.model.ProfileResponse
import com.example.myapplication.data.remote.model.RegisterRequest
import com.example.myapplication.util.Result
import com.example.myapplication.util.safeApiCall

class AuthRepository(
    private val api: ApiService,
    private val authStorage: AuthStorage
) {
    suspend fun login(email: String, password: String): Result<Unit> = safeApiCall {
        val response = api.login(LoginRequest(email, password))
        if (response.isSuccessful) {
            authStorage.saveToken(response.body()!!.access_token)
            Result.Success(Unit)
        } else {
            Result.Error(
                when (response.code()) {
                    401 -> "Invalid email or password"
                    else -> "Login failed (${response.code()})"
                }
            )
        }
    }

    suspend fun register(email: String, password: String): Result<String> = safeApiCall {
        val response = api.register(RegisterRequest(email, password))
        if (response.isSuccessful) {
            Result.Success(response.body()!!.message)
        } else {
            Result.Error(
                when (response.code()) {
                    409 -> "Email is already registered"
                    else -> "Registration failed (${response.code()})"
                }
            )
        }
    }

    suspend fun getProfile(): Result<ProfileResponse> = safeApiCall {
        val response = api.getProfile()
        if (response.isSuccessful) {
            Result.Success(response.body()!!)
        } else {
            Result.Error(
                when (response.code()) {
                    401 -> "Session expired — please log in again"
                    else -> "Failed to load profile (${response.code()})"
                },
                response.code()
            )
        }
    }

    suspend fun logout() {
        try { api.logout() } catch (_: Exception) {}
        authStorage.clearToken()
    }
}
