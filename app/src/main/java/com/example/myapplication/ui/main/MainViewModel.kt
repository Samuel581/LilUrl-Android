package com.example.myapplication.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.SmolifyApp
import com.example.myapplication.data.mock.MockLink
import com.example.myapplication.data.mock.toMockLink
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.UrlRepository
import com.example.myapplication.util.Result
import com.example.myapplication.util.UiState
import com.example.myapplication.util.ViewModelFactory
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val urlRepository: UrlRepository,
) : ViewModel() {

    // Navigation: 0 = Links tab, 1 = Account tab
    var selectedTab by mutableStateOf(0)
        private set

    // Non-null when Link Detail is showing
    var selectedLink by mutableStateOf<MockLink?>(null)
        private set

    // Link list
    var linksState by mutableStateOf<UiState<List<MockLink>>>(UiState.Loading)
        private set

    // Shorten form (used by the bottom sheet)
    var longUrl by mutableStateOf("")
    var slug by mutableStateOf("")
    var expiryDisplay by mutableStateOf("")
    var expiryIso by mutableStateOf<String?>(null)
    var urlError by mutableStateOf<String?>(null)
    var shortenState by mutableStateOf<UiState<String>>(UiState.Idle)
        private set

    // Profile
    var profileEmail by mutableStateOf("")
        private set

    init {
        loadLinks()
        loadProfile()
    }

    fun selectTab(index: Int) { selectedTab = index }

    fun openDetail(link: MockLink) { selectedLink = link }

    fun closeDetail() { selectedLink = null }

    fun loadLinks() {
        linksState = UiState.Loading
        viewModelScope.launch {
            linksState = when (val r = urlRepository.getLinks()) {
                is Result.Success -> UiState.Success(r.data.map { it.toMockLink() })
                is Result.Error -> UiState.Error(r.message, r.code)
            }
        }
    }

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
                    loadLinks()
                }
                is Result.Error -> shortenState = UiState.Error(r.message, r.code)
            }
        }
    }

    fun resetShortenState() { shortenState = UiState.Idle }

    fun deleteLink(link: MockLink) {
        val current = (linksState as? UiState.Success)?.data ?: return
        linksState = UiState.Success(current.filter { it.id != link.id })
        if (selectedLink?.id == link.id) selectedLink = null
    }

    private fun loadProfile() {
        viewModelScope.launch {
            when (val r = authRepository.getProfile()) {
                is Result.Success -> profileEmail = r.data.email
                is Result.Error -> Unit
            }
        }
    }

    private fun resetForm() {
        longUrl = ""; slug = ""; expiryDisplay = ""; expiryIso = null; urlError = null
    }

    companion object {
        fun factory(app: SmolifyApp) = ViewModelFactory {
            MainViewModel(app.authRepository, app.urlRepository)
        }
    }
}
