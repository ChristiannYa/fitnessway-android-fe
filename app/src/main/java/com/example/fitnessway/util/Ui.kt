package com.example.fitnessway.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

object Ui {

    @Composable
    fun <T> handleErrStateTempMsg(
        uiState: UiState<T>,
        onTimeOut: () -> Unit
    ): String {
        return when (uiState) {
            is UiState.Error -> {
                LaunchedEffect(uiState) {
                    delay(8000)
                    onTimeOut()
                }

                uiState.message
            } else -> ""
        }
    }

}