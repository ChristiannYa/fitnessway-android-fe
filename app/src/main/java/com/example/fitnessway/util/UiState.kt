package com.example.fitnessway.util

sealed class UiState<out T> {
   data object Idle: UiState<Nothing>()
   object Loading: UiState<Nothing>()
   data class Success<T>(val data: T) : UiState<T>()
   object Empty : UiState<Nothing>()
   data class Error(val message: String) : UiState<Nothing>()
}