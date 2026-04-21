package com.example.fitnessway.util

import com.example.fitnessway.data.model.m_26.PaginationResult

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()

    // @TODO: Rename to a more descriptive name
    val hasState get() = this is Success || this is Error
}

data class UiStatePager<T>(
    val uiState: UiState<PaginationResult<T>> = UiState.Loading,
    val isLoadingMore: Boolean = false
)