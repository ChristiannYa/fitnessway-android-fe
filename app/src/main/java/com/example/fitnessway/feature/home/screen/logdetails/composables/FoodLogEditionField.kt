package com.example.fitnessway.feature.home.screen.logdetails.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodLogEditionField

@Composable
fun FoodLogEditionField(field: FoodLogEditionField) {
    val textStyle = TextStyle(
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
        fontFamily = FontFamily.Default,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
    )

    val padding = 10.dp
    val shape = 12.dp

    BasicTextField(
        value = field.value,
        onValueChange = field.updateState,
        textStyle = textStyle,
        maxLines = 1,
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .clip(RoundedCornerShape(shape))
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(shape)
            )
            .padding(padding)
    )
}