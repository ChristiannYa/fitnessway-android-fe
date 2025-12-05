package com.example.fitnessway.util.form.field

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
private fun getDefaultStyle(): TextStyle {
    return TextStyle(
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
        fontFamily = FontFamily.Default,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
    )
}

@Composable
fun rememberFieldInputMeasures(
    fieldValue: String,
    customTextStyle: TextStyle? = null
): Triple<TextMeasurer, Density, Dp> {
    val textStyle = customTextStyle ?: getDefaultStyle()

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val measuredWidth = remember(fieldValue, textStyle) {
        val textLayoutResult = textMeasurer.measure(
            text = fieldValue.ifEmpty { "~" },
            style = textStyle
        )

        // Convert pixels to dp using density
        with(density) {
            textLayoutResult.size.width.toDp() + 16.dp
        }
    }

    return Triple(textMeasurer, density, measuredWidth)
}