package com.example.fitnessway.feature.profile.screen.goals.composables

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FormField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.util.Nutrient.getNutrientColor
import com.example.fitnessway.util.form.field.rememberFieldInputMeasures

@Composable
fun GoalsEditionFormField(
    field: FormField<FormFieldName.NutrientGoalData>,
    enabled: Boolean = true,
) {
    val goalData = field.name.nutrientData
    val nutrient = goalData.nutrient

    val goalTextColor = getNutrientColor(nutrient.hexColor)
        ?: MaterialTheme.colorScheme.primary

    val padding = 12.dp

    val inputMeasures = rememberFieldInputMeasures(
        inputValue = field.value,
        inputPadding = padding,
        inputColor = goalTextColor
    )

    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        content = {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Medium
                        ),
                        block = { append("${nutrient.name} ") }
                    )

                    if (nutrient.type == NutrientType.MINERAL) {
                        append(text = "(${nutrient.symbol}) ")
                    }

                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onBackground.copy(
                                0.6f
                            )
                        ),
                        block = { append(text = nutrient.unit) }
                    )
                },
                style = MaterialTheme.typography.bodyMedium
            )

            BasicTextField(
                value = field.value,
                onValueChange = field.updateState,
                enabled = enabled,
                textStyle = inputMeasures.textStyle,
                singleLine = true,
                modifier = Modifier
                    .clip(shape)
                    .width(inputMeasures.measuredWidth)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = shape
                    )
                    .padding(vertical = padding),
            )
        }
    )
}