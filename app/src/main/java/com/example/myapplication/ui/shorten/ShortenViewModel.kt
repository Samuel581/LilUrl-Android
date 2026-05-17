package com.example.myapplication.ui.shorten

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.mock.MOCK_ALL_TAGS
import com.example.myapplication.data.mock.MockLink
import com.example.myapplication.data.remote.model.UrlItem
import com.example.myapplication.data.repository.UrlRepository
import com.example.myapplication.util.Result
import com.example.myapplication.util.UiState
import com.example.myapplication.util.ViewModelFactory
import kotlinx.coroutines.launch

class ShortenViewModel(private val urlRepository: UrlRepository) : ViewModel() {

    // Form state
    var longUrl by mutableStateOf("")
    var slug by mutableStateOf("")
    var expiryDisplay by mutableStateOf("")
    var expiryIso by mutableStateOf<String?>(null)
    var tags by mutableStateOf<List<String>>(emptyList())
    var tagInput by mutableStateOf("")
    var passwordEnabled by mutableStateOf(false)
    var password by mutableStateOf("")
    var urlError by mutableStateOf<String?>(null)

    // Filter
    var selectedFilterTag by mutableStateOf<String?>(null)

    // Real link list from API
    var linksState by mutableStateOf<UiState<List<MockLink>>>(UiState.Loading)
        private set

    // Shorten result state
    var shortenState by mutableStateOf<UiState<String>>(UiState.Idle)
        private set

    init { loadLinks() }

    fun loadLinks() {
        linksState = UiState.Loading
        viewModelScope.launch {
            linksState = when (val r = urlRepository.getLinks()) {
                is Result.Success -> UiState.Success(r.data.map { it.toMockLink() })
                is Result.Error -> UiState.Error(r.message, r.code)
            }
        }
    }

    val allTagSuggestions: List<String>
        get() = MOCK_ALL_TAGS.filter { it !in tags }

    val filteredLinks: List<MockLink>
        get() {
            val all = (linksState as? UiState.Success)?.data ?: return emptyList()
            return if (selectedFilterTag == null) all else all.filter { selectedFilterTag in it.tags }
        }

    fun addTag(tag: String) {
        val t = tag.trim()
        if (t.isNotEmpty() && t !in tags) tags = tags + t
        tagInput = ""
    }

    fun removeTag(tag: String) { tags = tags - tag }

    fun shorten() {
        val url = longUrl.trim()
        if (url.isEmpty()) { urlError = "Enter a URL"; return }
        urlError = null
        shortenState = UiState.Loading
        viewModelScope.launch {
            when (val r = urlRepository.shorten(url, slug.trim().takeIf { it.isNotEmpty() }, expiryIso)) {
                is Result.Success -> {
                    resetForm()
                    shortenState = UiState.Success(r.data.shortUrl)
                    loadLinks() // refresh real list from API
                }
                is Result.Error -> shortenState = UiState.Error(r.message, r.code)
            }
        }
    }

    fun resetShortenState() { shortenState = UiState.Idle }

    private fun resetForm() {
        longUrl = ""; slug = ""; expiryDisplay = ""; expiryIso = null; tags = emptyList(); password = ""
    }

    companion object {
        fun factory(repo: UrlRepository) = ViewModelFactory { ShortenViewModel(repo) }
    }
}

private fun UrlItem.toMockLink() = MockLink(
    id = shortCode,
    shortUrl = shortUrl,
    shortCode = shortCode,
    originalUrl = originalUrl,
    domain = extractDomain(originalUrl),
    tags = emptyList(),
    isPasswordProtected = false,
    healthStatus = -1,  // not checked — no health endpoint yet
    totalClicks = -1,   // not tracked — no analytics endpoint yet
    createdAt = createdAt ?: "—",
    expiresAt = expiresAt,
)

private fun extractDomain(url: String): String =
    runCatching { url.removePrefix("https://").removePrefix("http://").substringBefore("/") }.getOrElse { url }
