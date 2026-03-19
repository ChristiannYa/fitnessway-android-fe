package com.example.fitnessway.util.food.creation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import com.example.fitnessway.util.INutrientDvControls
import com.example.fitnessway.util.UNutrient.Ui.NutrientFieldLabel
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.form.field.FormField
import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.util.nutrient.DvTrailingIcon
import com.example.fitnessway.util.nutrient.rememberNutrientDvState

@Composable
fun <T : FormFieldName.IFoodCreation> FoodCreationFormField(
    field: FormField<T>,
    nutrientDvControls: INutrientDvControls.NutrientDvControls? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val outlinedColors = Ui.InputUi.getOutlinedColors()
    val inputShape = Ui.InputUi.shape
    val inputTextStyle = Ui.InputUi.getTextStyle()

    val nutrientDvState = rememberNutrientDvState(
        field = field,
        nutrientDvControls = nutrientDvControls,
        getNutrient = { name ->
            if (name is FormFieldName.FoodCreation.NutrientField) {
                name.nutrientWithPreferences.nutrient
            } else null
        }
    )

    if (field.textFieldValue != null && field.updateTextFieldValueState != null) {
        OutlinedTextField(
            value = field.textFieldValue,
            onValueChange = field.updateTextFieldValueState,
            enabled = field.enabled,
            label = {
                if (nutrientDvState.nutrient != null) NutrientFieldLabel(
                    nutrient = nutrientDvState.nutrient,
                    isFocused = isFocused,
                    isInDvMode = nutrientDvState.isInNutrientDvMap
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
            trailingIcon = DvTrailingIcon(nutrientDvState, field.textFieldValue.text),
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