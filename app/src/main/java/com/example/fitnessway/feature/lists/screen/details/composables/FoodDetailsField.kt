package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FormField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.field.rememberFieldInputMeasures

@Composable
fun <T : FormFieldName.IFoodEdition> FoodDetailsField(
    field: FormField<T>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onRemoveNutrient: ((nutrientId: Int) -> Unit)? = null
) {
    val padding = 12.dp
    val shape = 12.dp

    val inputMeasures = rememberFieldInputMeasures(
        inputValue = field.value,
        inputPadding = padding
    )

    val isNutrient = field.name is FormFieldName.FoodEdition.NutrientField

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        content = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    if (isNutrient) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        val nutrientId = field.name.nutrient.id
                                        onRemoveNutrient?.invoke(nutrientId)
                                    },
                                ),
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(16.dp)
                                )
                            }
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
                textStyle = inputMeasures.textStyle,
                singleLine = true,
                modifier = modifier
                    .width(inputMeasures.measuredWidth)
                    .clip(RoundedCornerShape(shape))
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(shape)
                    )
                    .padding(vertical = padding)
            )
        }
    )
}