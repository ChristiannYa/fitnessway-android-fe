package com.example.fitnessway.feature.lists.screen.create.food.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import com.example.fitnessway.util.UNutrient.Ui.NutrientFieldLabel
import com.example.fitnessway.util.UNutrient.hasDailyValue
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.form.field.FormField
import com.example.fitnessway.util.form.field.FormFieldName

@Composable
fun <T : FormFieldName.IFoodCreation> FoodCreationFormField(
    field: FormField<T>,
    foodCreationNutrientsAsPercentages: Map<Int, String>? = null,
    onAddToPercentagesMap: ((nutrientId: Int, amount: String) -> Unit)? = null,
    onRemoveFromPercentagesMap: ((nutrientId: Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isNutrient = field.name is FormFieldName.FoodCreation.NutrientField

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val outlinedColors = Ui.InputUi.getOutlinedColors()
    val inputShape = Ui.InputUi.shape
    val inputTextStyle = Ui.InputUi.getTextStyle()

    val nutrient = if (isNutrient) field.name.nutrientWithPreferences.nutrient else null
    val isInPercentagesMap = nutrient?.let {
        foodCreationNutrientsAsPercentages?.containsKey(it.id) ?: false
    } ?: false

    if (nutrient != null &&
        nutrient.hasDailyValue &&
        field.textFieldValue != null
    ) {
        LaunchedEffect(field.textFieldValue.text, isInPercentagesMap) {
            if (isInPercentagesMap && field.textFieldValue.text.isEmpty()) {
                onRemoveFromPercentagesMap?.invoke(nutrient.id)
            }
        }
    }

    if (field.textFieldValue != null && field.updateTextFieldValueState != null) {
        OutlinedTextField(
            value = field.textFieldValue,
            onValueChange = field.updateTextFieldValueState,
            enabled = field.enabled,
            label = {
                if (nutrient != null) NutrientFieldLabel(
                    nutrient = nutrient,
                    isFocused = isFocused,
                    extraFieldText = if (isInPercentagesMap) "(%DV)" else null
                ) else Text(
                    text = field.label,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            supportingText = if (field.errorMessage != null) {
                {
                    Text(
                        text = field.errorMessage,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else null,
            trailingIcon = if (nutrient != null && nutrient.hasDailyValue) {
                {
                    NutrientUnitToggle(
                        isInPercentagesMap = isInPercentagesMap,
                        enabled = field.textFieldValue.text.isNotEmpty(),
                        onToggle = { shouldBeInMap ->
                            if (shouldBeInMap) {
                                onAddToPercentagesMap?.invoke(
                                    nutrient.id,
                                    field.textFieldValue.text
                                )
                            } else {
                                onRemoveFromPercentagesMap?.invoke(nutrient.id)
                            }
                        }
                    )
                }
            } else null,
            keyboardOptions = field.keyboardOptions,
            keyboardActions = field.keyboardActions,
            interactionSource = interactionSource,
            textStyle = inputTextStyle,
            singleLine = true,
            shape = inputShape,
            colors = outlinedColors,
            modifier = modifier
                .fillMaxWidth()
                .then(
                    if (field.focusRequester != null) {
                        Modifier.focusRequester(field.focusRequester)
                    } else Modifier
                ),
        )
    }
}