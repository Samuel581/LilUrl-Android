package com.example.myapplication.data.remote.model

data class RegisterRequest(val email: String, val password: String)
data class RegisterResponse(val message: String)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val access_token: String)

data class LogoutResponse(val message: String)

data class ProfileResponse(
    val id: String,
    val email: String,
    val createdAt: String? = null
)
