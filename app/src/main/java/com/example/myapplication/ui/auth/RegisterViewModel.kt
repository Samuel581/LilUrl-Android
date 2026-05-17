package com.example.myapplication.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.util.Result
import com.example.myapplication.util.UiState
import com.example.myapplication.util.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState<String>>(UiState.Idle)
    val state: StateFlow<UiState<String>> = _state

    fun register(email: String, password: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            _state.value = when (val r = repository.register(email, password)) {
                is Result.Success -> UiState.Success(r.data)
                is Result.Error -> UiState.Error(r.message)
            }
        }
    }

    companion object {
        fun factory(repo: AuthRepository) = ViewModelFactory { RegisterViewModel(repo) }
    }
}
