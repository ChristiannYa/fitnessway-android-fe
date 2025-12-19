package com.example.fitnessway.feature.home.screen.create.food.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FormField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.ui.shared.PremiumIcon
import com.example.fitnessway.util.Ui

@Composable
fun <T : FormFieldName.IFoodCreation> FoodCreationFormField(
    field: FormField<T>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val inputTextStyle = Ui.InputUi.getTextStyle(textAlign = TextAlign.End)
    val padding = 16.dp
    val isNutrient = field.name is FormFieldName.FoodCreation.NutrientField

    val label = if (isNutrient) {
        val nutrient = field.name.nutrientWithPreferences.nutrient

        buildAnnotatedString {
            append("${nutrient.name} ")

            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
                )
            ) {
                append(nutrient.unit)
            }
        }
    } else {
        AnnotatedString(field.label)
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
                    if (!enabled) PremiumIcon()

                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )

            BasicTextField(
                value = field.value,
                onValueChange = field.updateState,
                enabled = enabled,
                textStyle = inputTextStyle,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            )
        }
    )
}