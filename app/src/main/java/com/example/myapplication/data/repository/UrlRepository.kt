package com.example.myapplication.data.repository

import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.NetworkClient
import com.example.myapplication.data.remote.model.ShortenRequest
import com.example.myapplication.data.remote.model.ShortenResponse
import com.example.myapplication.data.remote.model.UrlItem
import com.example.myapplication.util.Result
import com.example.myapplication.util.safeApiCall

class UrlRepository(private val api: ApiService) {
    suspend fun getLinks(): Result<List<UrlItem>> = safeApiCall {
        val response = api.getLinks()
        if (response.isSuccessful) {
            val body = response.body() ?: emptyList()
            val corrected = body.map { item ->
                item.copy(shortUrl = "${NetworkClient.ROOT_URL}s/${item.shortCode}")
            }
            Result.Success(corrected)
        } else {
            Result.Error(
                when (response.code()) {
                    401 -> "Session expired — please log in again"
                    else -> "Failed to load links (${response.code()})"
                },
                response.code()
            )
        }
    }

    suspend fun shorten(
        originalUrl: String,
        customAlias: String?,
        expiresAt: String?
    ): Result<ShortenResponse> = safeApiCall {
        val response = api.shorten(
            ShortenRequest(
                originalUrl = originalUrl,
                customAlias = customAlias?.takeIf { it.isNotBlank() },
                expiresAt = expiresAt
            )
        )
        if (response.isSuccessful) {
            val body = response.body()!!
            // Server returns its internal hostname in shortUrl; rebuild from shortCode using /:code redirect
            val corrected = body.copy(
                shortUrl = "${NetworkClient.ROOT_URL}s/${body.shortCode}"
            )
            Result.Success(corrected)
        } else {
            Result.Error(
                when (response.code()) {
                    400 -> "Invalid URL or expiry date"
                    401 -> "Session expired — please log in again"
                    409 -> "That custom alias is already taken"
                    else -> "Something went wrong (${response.code()})"
                },
                response.code()
            )
        }
    }
}
