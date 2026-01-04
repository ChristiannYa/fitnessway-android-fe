package com.example.fitnessway.util.form.field.provider

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.form.NutrientColorUpdateField
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates

class NutrientColorsFieldsProvider(
    private val formState: FormState<FormStates.NutrientColors>,
    private val onFieldUpdate: (FormFieldName.NutrientColorUpdate, String) -> Unit,
    private val focusManager: FocusManager,
    private val isFormSubmitting: Boolean
) {
    @Composable
    fun nutrientColor(
        nutrientData: NutrientWithPreferences,
        isLastField: Boolean
    ): NutrientColorUpdateField {
        val value = formState.data.colors[nutrientData.nutrient.id] ?: "~"

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = nutrientData.nutrient.id
        )

        val nutrient = nutrientData.nutrient

        return NutrientColorUpdateField(
            name = FormFieldName.NutrientColorUpdate(nutrientData),
            label = "${nutrient.name} ${nutrient.unit}",
            value = value,
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
            updateState = { value ->
                val truncatedValue = value.take(6).uppercase()

                onFieldUpdate(
                    FormFieldName.NutrientColorUpdate(nutrientData),
                    truncatedValue
                )
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.NutrientColorUpdate(nutrientData),
                    it.text
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = if (isLastField) ImeAction.Done else ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = if (!isLastField) {
                    { focusManager.moveFocus(FocusDirection.Down) }
                } else null,
                onDone = if (isLastField) {
                    { focusManager.clearFocus() }
                } else null
            ),
        )
    }
}