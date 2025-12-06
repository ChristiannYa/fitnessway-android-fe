package com.example.fitnessway.util.form.field

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun rememberFieldInputMeasures(
    inputValue: String,
    inputPadding: Dp = 16.dp,
    inputColor: Color = MaterialTheme.colorScheme.primary,
    customTextStyle: TextStyle? = null
): FieldInputMeasures {
    val textStyle = customTextStyle ?: getDefaultStyle(color = inputColor)

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val measuredWidth = remember(inputValue, textStyle) {
        val textLayoutResult = textMeasurer.measure(
            text = inputValue.ifEmpty { "~" },
            style = textStyle
        )

        // Convert pixels to dp using density
        with(density) {
            textLayoutResult.size.width.toDp() + (inputPadding * 2)
        }
    }

    return FieldInputMeasures(textStyle, textMeasurer, density, measuredWidth)
}

data class FieldInputMeasures(
    val textStyle: TextStyle,
    val textMeasurer: TextMeasurer,
    val density: Density,
    val measuredWidth: Dp
)

@Composable
private fun getDefaultStyle(color: Color): TextStyle {
    return TextStyle(
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
        fontFamily = FontFamily.Default,
        color = color,
        textAlign = TextAlign.Center
    )
}