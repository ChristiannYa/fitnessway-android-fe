package com.example.fitnessway.feature.home.screen.foodselection.foodlog.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodLogField

@Composable
fun FoodLogBaseInformationField(
    field: FoodLogField,
    isEditing: Boolean,
) {
    val backgroundColor =
        if (isEditing) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent
    val shape = if (isEditing) 10.dp else 0.dp
    val padding = if (isEditing) 14.dp else 0.dp

    val textStyle = TextStyle(
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        fontFamily = FontFamily.Default,
        color = MaterialTheme.colorScheme.primary,
    )

    // Measure the actual text width
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val measuredWidth = remember(field.value, textStyle) {
        val textLayoutResult = textMeasurer.measure(
            text = field.value.ifEmpty { "0" },
            style = textStyle
        )

        // Convert pixels to dp using density
        with(density) {
            textLayoutResult.size.width.toDp()
        }
    }

    BasicTextField(
        value = field.value,
        onValueChange = field.updateState,
        textStyle = textStyle,
        singleLine = true,
        enabled = isEditing,
        modifier = Modifier
            .width(measuredWidth + padding + if (isEditing) 16.dp else 0.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(shape)
            )
            .padding(padding)
    )
}