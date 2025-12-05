package com.example.fitnessway.feature.home.screen.create.food.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
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
import com.example.fitnessway.data.model.form.FormField
import com.example.fitnessway.data.model.form.FormFieldName

@Composable
fun <T : FormFieldName.IFoodCreation> FoodCreationFormField(
    field: FormField<T>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val textStyle = TextStyle(
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        fontFamily = FontFamily.Default,
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
        letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
    )

    val padding = 16.dp

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val measuredWidth = remember(field.value, textStyle) {
        val textLayoutResult = textMeasurer.measure(
            text = field.value.ifEmpty { "0" },
            style = textStyle
        )

        // Convert pixels to dp using density
        with(density) {
            textLayoutResult.size.width.toDp() + (padding * 2)
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(
                PaddingValues(
                    start = padding,
                )
            ),
        content = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    if (!enabled) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = field.label,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = padding)
                    )
                }
            )

            BasicTextField(
                value = field.value,
                onValueChange = field.updateState,
                enabled = enabled,
                textStyle = textStyle,
                singleLine = true,
                modifier = Modifier
                    .width(measuredWidth)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    .padding(vertical = padding)
            )
        }
    )
}