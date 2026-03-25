package com.example.fitnessway.data.mappers

import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager

fun <U> UiState<U>.toErrorMessageOrNull() = (this as? UiState.Error)?.message

fun <U> UiStatePager<U>.toPaginationOrNull() = (this.uiState as? UiState.Success)?.data