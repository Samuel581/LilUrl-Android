package com.example.myapplication.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.model.ShortenResponse
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.UrlRepository
import com.example.myapplication.util.Result
import com.example.myapplication.util.UiState
import com.example.myapplication.util.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val urlRepository: UrlRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _shortenState = MutableStateFlow<UiState<ShortenResponse>>(UiState.Idle)
    val shortenState: StateFlow<UiState<ShortenResponse>> = _shortenState

    private val _logoutState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val logoutState: StateFlow<UiState<Unit>> = _logoutState

    fun shorten(originalUrl: String, customAlias: String?, expiresAt: String?) {
        _shortenState.value = UiState.Loading
        viewModelScope.launch {
            _shortenState.value = when (val r = urlRepository.shorten(originalUrl, customAlias, expiresAt)) {
                is Result.Success -> UiState.Success(r.data)
                is Result.Error -> UiState.Error(r.message, r.code)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _logoutState.value = UiState.Success(Unit)
        }
    }

    fun resetShortenState() {
        _shortenState.value = UiState.Idle
    }

    companion object {
        fun factory(urlRepo: UrlRepository, authRepo: AuthRepository) =
            ViewModelFactory { HomeViewModel(urlRepo, authRepo) }
    }
}
