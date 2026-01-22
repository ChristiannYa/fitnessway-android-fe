package com.example.fitnessway.util

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// usage: nutrientsUiState.isSuccess
val <T> UiState<T>.isSuccess: Boolean get() = this is UiState.Success
val <T> UiState<T>.isLoading: Boolean get() = this is UiState.Loading
val <T> UiState<T>.isError: Boolean get() = this is UiState.Error
val <T> UiState<T>.isIdle: Boolean get() = this is UiState.Idle

// usage: nutrientsUiState.isSuccess()
// fun <T> UiState<T>.isSuccess(): Boolean = this is UiState.Success