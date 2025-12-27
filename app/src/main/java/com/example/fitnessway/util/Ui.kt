package com.example.fitnessway.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

object Ui {
    enum class LabelSize(
        val cornerRadius: Int,
        val paddingX: Dp,
        val paddingY: Dp
    ) {
        LARGE(60, 14.dp, 10.dp),
        MEDIUM(50, 12.dp, 8.dp),
        SMALL(40, 10.dp, 6.dp)
    }

    object Measurements {
        val SCREEN_HORIZONTAL_PADDING = 16.dp
        val TEXT_ICON_HORIZONTAL_SPACE = 10.dp
        val PROGRESS_INDICATOR_SIZE = 26.dp
        val PROGRESS_INDICATOR_WIDTH = 2.dp
    }

    object InputUi {
        val padding = 12.dp
        val roundedSize = 10.dp
        val shape = RoundedCornerShape(roundedSize)

        @Composable
        fun getColor(): Color {
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
    fun <T> handleApiErrorTempMessage(
        uiState: UiState<T>,
        onTimeOut: () -> Unit
    ): String? {
        LaunchedEffect(uiState) {
            if (uiState is UiState.Error) {
                delay(8000)
                onTimeOut()
            }
        }

        return if (uiState is UiState.Error) uiState.message else null
    }

    @Composable
    fun <T> handleApiSuccessTempState(
        uiState: UiState<T>,
        delayMillis: Long = 6000,
        onTimeout: () -> Unit
    ): Boolean {
        var shouldShow by remember { mutableStateOf(false) }

        LaunchedEffect(uiState) {
            if (uiState is UiState.Success) {
                shouldShow = true
                delay(delayMillis)
                shouldShow = false
                onTimeout()
            }
        }

        return shouldShow
    }


    @Composable
    fun AppLabel(
        text: String,
        color: Color = MaterialTheme.colorScheme.surfaceVariant,
        size: LabelSize = LabelSize.MEDIUM,
        textStyle: TextStyle = MaterialTheme.typography.labelMedium,
        modifier: Modifier = Modifier
    ) {
        Surface(
            shape = RoundedCornerShape(size.cornerRadius),
            color = color,
            modifier = modifier
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = text,
                    style = textStyle,
                    modifier = Modifier.padding(
                        horizontal = size.paddingX,
                        vertical = size.paddingY
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                )
            }
        }
    }
}