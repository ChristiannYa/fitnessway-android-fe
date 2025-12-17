package com.example.fitnessway.util

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

object Ui {
    object Measurements {
        val SCREEN_HORIZONTAL_PADDING = 16.dp
        val TEXT_ICON_HORIZONTAL_SPACE = 10.dp
    }

    object InputUi {
        val padding = 12.dp
        val roundedSize = 10.dp
        val shape = RoundedCornerShape(roundedSize)

        @Composable
        fun getMaterialBackground(): Color {
            return MaterialTheme.colorScheme.surfaceVariant
        }

        @Composable
        fun getTextStyle(
            textColor: Color = MaterialTheme.colorScheme.primary,
            textAlign: TextAlign = TextAlign.Start
        ): TextStyle {
            return TextStyle(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                fontFamily = FontFamily.Default,
                color = textColor,
                textAlign = textAlign
            )
        }
    }

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
            }

            else -> ""
        }
    }

}