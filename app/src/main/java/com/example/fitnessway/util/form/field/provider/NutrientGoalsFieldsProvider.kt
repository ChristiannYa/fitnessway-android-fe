package com.example.fitnessway.util.form.field.provider

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.util.form.field.NutrientGoalEditionField
import com.example.fitnessway.data.model.MNutrient.Model.NutrientWithPreferences
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates

class NutrientGoalsFieldsProvider(
    private val formState: FormState<FormStates.NutrientGoals>,
    private val onFieldUpdate: (FormFieldName.NutrientGoalData, String) -> Unit,
    private val focusManager: FocusManager,
    private val isFormSubmitting: Boolean
) {
    @Composable
    fun nutrientGoal(
        nutrientData: NutrientWithPreferences,
        isLastField: Boolean
    ): NutrientGoalEditionField {
        val value = formState.data.goals[nutrientData.nutrient.id] ?: "~"

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = nutrientData.nutrient.id
        )

        val nutrient = nutrientData.nutrient

        return NutrientGoalEditionField(
            name = FormFieldName.NutrientGoalData(nutrientData),
            label = "${nutrient.name} ${nutrient.unit}",
            value = value,
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
            updateState = { value ->
                onFieldUpdate(
                    FormFieldName.NutrientGoalData(nutrientData),
                    value
                )
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.NutrientGoalData(nutrientData),
                    it.text
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = if (isLastField) ImeAction.Done else ImeAction.Next,
                keyboardType = KeyboardType.Decimal
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