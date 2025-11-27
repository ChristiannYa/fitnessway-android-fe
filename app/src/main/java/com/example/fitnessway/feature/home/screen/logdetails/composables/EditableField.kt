package com.example.fitnessway.feature.home.screen.logdetails.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodLogEditionField

@Composable
fun EditableField(field: FoodLogEditionField) {
    val textStyle = TextStyle(
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
        fontFamily = FontFamily.Default,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
    )

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val padding = 10.dp
    val shape = 12.dp

    val measuredWidth = remember(field.value, textStyle) {
        val textLayoutResult = textMeasurer.measure(
            text = field.value.ifEmpty { "0" },
            style = textStyle
        )

        // Convert pixels to dp using density
        with(density) {
            textLayoutResult.size.width.toDp() + 16.dp
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        content = {
            Text(
                text = field.label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = padding)
            )

            BasicTextField(
                value = field.value,
                onValueChange = field.updateState,
                textStyle = textStyle,
                singleLine = true,
                modifier = Modifier
                    .width(measuredWidth + padding)
                    .clip(RoundedCornerShape(shape))
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(shape)
                    )
                    .padding(padding)
            )
        }
    )
}