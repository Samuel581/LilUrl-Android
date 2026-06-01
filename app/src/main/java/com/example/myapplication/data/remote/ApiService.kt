package com.example.myapplication.data.remote

import com.example.myapplication.data.remote.model.LoginRequest
import com.example.myapplication.data.remote.model.LoginResponse
import com.example.myapplication.data.remote.model.LogoutResponse
import com.example.myapplication.data.remote.model.ProfileResponse
import com.example.myapplication.data.remote.model.RegisterRequest
import com.example.myapplication.data.remote.model.RegisterResponse
import com.example.myapplication.data.remote.model.ShortenRequest
import com.example.myapplication.data.remote.model.ShortenResponse
import com.example.myapplication.data.remote.model.Link
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<LogoutResponse>

    @POST("url/shorten")
    suspend fun shorten(@Body request: ShortenRequest): Response<ShortenResponse>

    @GET("url")
    suspend fun getLinks(): Response<List<Link>>

    @GET("profile")
    suspend fun getProfile(): Response<ProfileResponse>
}
