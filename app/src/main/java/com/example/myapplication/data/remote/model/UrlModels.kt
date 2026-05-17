package com.example.myapplication.data.remote.model

data class ShortenRequest(
    val originalUrl: String,
    val customAlias: String? = null,
    val expiresAt: String? = null
)

data class ShortenResponse(
    val shortUrl: String,
    val shortCode: String,
    val originalUrl: String,
    val expiresAt: String? = null
)

data class UrlItem(
    val shortUrl: String,
    val shortCode: String,
    val originalUrl: String,
    val expiresAt: String? = null,
    val createdAt: String? = null
)
