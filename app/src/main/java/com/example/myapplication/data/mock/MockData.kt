package com.example.myapplication.data.mock

import com.example.myapplication.data.remote.model.UrlItem
import java.net.URI

data class MockLink(
    val id: String,
    val shortUrl: String,
    val shortCode: String,
    val originalUrl: String,
    val domain: String,
    val tags: List<String> = emptyList(),
    val isPasswordProtected: Boolean = false,
    val healthStatus: Int = 200,
    val totalClicks: Int = 0,
    val createdAt: String,
    val expiresAt: String? = null,
)

fun UrlItem.toMockLink(): MockLink {
    val domain = try { URI(originalUrl).host ?: originalUrl } catch (e: Exception) { originalUrl }
    return MockLink(
        id = shortCode,
        shortUrl = shortUrl,
        shortCode = shortCode,
        originalUrl = originalUrl,
        domain = domain,
        createdAt = createdAt ?: "",
        expiresAt = expiresAt,
    )
}
