package com.example.fitnessway.util.extensions

import com.example.fitnessway.util.UiState

fun <T> UiState<T>.getStateName(): String = when (this) {
    is UiState.Loading -> "Loading"
    is UiState.Success -> "Success"
    is UiState.Error -> "Error"
    is UiState.Idle -> "Idle"
}