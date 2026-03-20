package com.example.fitnessway.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
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
        Xl(40, 16.dp, 12.dp),
        LARGE(40, 14.dp, 10.dp),
        MEDIUM(40, 12.dp, 8.dp),
        SMALL(40, 10.dp, 6.dp),
        XS(40, 8.dp, 4.dp)
    }

    object Measurements {
        val SCREEN_HORIZONTAL_PADDING = 16.dp
        val TEXT_ICON_HORIZONTAL_SPACE = 10.dp
        val LOADING_CIRCLE_IN_HEADER_SIZE = 21.dp
        val LOADING_CIRCLE_IN_HEADER_STROKE_WIDTH = 2.dp
        val LOADING_CIRCLE_IN_SCREEN_SIZE = 32.dp
        val LOADING_CIRCLE_IN_SCREEN_STROKE_WIDTH = 3.dp
        val UPWARDS_SLIDEABLE_HEIGHT_SMALL = 480.dp
    }

    object InputUi {
        val padding = 12.dp
        val roundedSize = 10.dp
        val shape = RoundedCornerShape(roundedSize)

        @Composable
        fun getBackgroundColor(): Color {
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

        @Composable
        fun getOutlinedColors(): TextFieldColors {
            val inputColor = this.getBackgroundColor()

            return OutlinedTextFieldDefaults
                .colors(
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = inputColor.copy(0.6f),
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                    disabledBorderColor = Color.Transparent,
                    disabledContainerColor = inputColor.copy(0.6f),
                    disabledLabelColor = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = inputColor,
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                )
        }

        @Composable
        fun getBackgroundColorAnimated(
            isFocused: Boolean,
            isEnabled: Boolean,
            label: String
        ): Color {
            val inputBackgroundColor = this.getBackgroundColor()

            val color by animateColorAsState(
                targetValue = if (isFocused) {
                    inputBackgroundColor
                } else if (isEnabled) inputBackgroundColor.copy(0.6f) else {
                    inputBackgroundColor.copy(0.6f)
                },
                animationSpec = Animation.colorSpec,
                label = label + "_backgroundColor"
            )

            return color
        }

        @Composable
        fun getBorderColorAnimated(
            isFocused: Boolean,
            label: String
        ): Color {
            val inputTextStyle = this.getTextStyle()

            val color by animateColorAsState(
                targetValue = if (isFocused) {
                    inputTextStyle.color
                } else Color.Transparent,
                animationSpec = Animation.colorSpec,
                label = label + "_borderColor"
            )

            return color
        }

        @Composable
        fun rememberTextFieldValueWithCursor(
            text: String,
            key: Any? = null
        ): MutableState<TextFieldValue> {
            val textFieldValue = remember(key) {
                mutableStateOf(
                    TextFieldValue(
                        text = text,
                        selection = TextRange(text.length)
                    )
                )
            }

            LaunchedEffect(text) {
                if (textFieldValue.value.text != text) {
                    textFieldValue.value = TextFieldValue(
                        text = text,
                        selection = TextRange(text.length)
                    )
                }
            }

            return textFieldValue
        }
    }

    @Composable
    fun <T> handleTempApiErrMsg(
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
    fun <T> AppLabel(
        text: String,
        color: Color = MaterialTheme.colorScheme.surfaceVariant,
        size: LabelSize = LabelSize.MEDIUM,
        textStyle: TextStyle = MaterialTheme.typography.labelMedium,
        clickableConfiguration: ClickableConfiguration<T>? = null,
        data: T? = null,
        modifier: Modifier = Modifier
    ) {
        Surface(
            shape = RoundedCornerShape(size.cornerRadius),
            color = color,
            modifier = modifier
                .clip(RoundedCornerShape(size.cornerRadius))
                .then(
                    if (clickableConfiguration != null && data != null) {
                        Modifier.clickable(
                            interactionSource = if (clickableConfiguration.showsClickIndication(data)) null else {
                                remember { MutableInteractionSource() }
                            },
                            indication = if (clickableConfiguration.showsClickIndication(data)) LocalIndication.current else null,
                            enabled = clickableConfiguration.enabled(data),
                            onClick = { clickableConfiguration.onClick(data) }
                        )
                    } else Modifier
                )
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

    data class ClickableConfiguration<T>(
        val onClick: (T) -> Unit,
        val enabled: (T) -> Boolean = { true },
        val showsClickIndication: (T) -> Boolean = { true }
    )
}