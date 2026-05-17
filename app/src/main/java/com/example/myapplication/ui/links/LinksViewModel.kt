package com.example.myapplication.ui.links

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.model.UrlItem
import com.example.myapplication.data.repository.UrlRepository
import com.example.myapplication.util.Result
import com.example.myapplication.util.UiState
import com.example.myapplication.util.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LinksViewModel(private val urlRepository: UrlRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<UrlItem>>>(UiState.Loading)
    val state: StateFlow<UiState<List<UrlItem>>> = _state

    init { load() }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            _state.value = when (val r = urlRepository.getLinks()) {
                is Result.Success -> UiState.Success(r.data)
                is Result.Error -> UiState.Error(r.message, r.code)
            }
        }
    }

    companion object {
        fun factory(urlRepo: UrlRepository) = ViewModelFactory { LinksViewModel(urlRepo) }
    }
}
