package com.example.fitnessway.util.form.field.provider

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.fitnessway.util.form.field.FoodLogField
import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates

class FoodLogFieldsProvider(
    private val formState: FormState<FormStates.FoodLog>,
    private val onFieldUpdate: (FormFieldName.FoodLog, String) -> Unit,
    private val focusManager: FocusManager,
    private val isFormSubmitting: Boolean
) {
    @Composable
    fun servings(): FoodLogField {
        val value = formState.data.servings

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodLog.SERVINGS
        )

        return FoodLogField(
            name = FormFieldName.FoodLog.SERVINGS,
            label = "Servings",
            value = formState.data.servings,
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
            updateState = { newValue ->
                onFieldUpdate(FormFieldName.FoodLog.SERVINGS, newValue)
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(FormFieldName.FoodLog.SERVINGS, it.text)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
        )
    }

    @Composable
    fun amountPerServing(servingUnit: String): FoodLogField {
        val value = formState.data.amountPerServing

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodLog.AMOUNT_PER_SERVING
        )

        return FoodLogField(
            name = FormFieldName.FoodLog.AMOUNT_PER_SERVING,
            label = "Amount Per Serving ($servingUnit)",
            value = formState.data.amountPerServing,
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
            updateState = { newValue ->
                onFieldUpdate(FormFieldName.FoodLog.AMOUNT_PER_SERVING, newValue)
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(FormFieldName.FoodLog.AMOUNT_PER_SERVING, it.text)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
        )
    }

    @Composable
    fun time(): FoodLogField {
        val value = formState.data.time

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodLog.TIME
        )

        return FoodLogField(
            name = FormFieldName.FoodLog.TIME,
            label = "Time",
            value = formState.data.time,
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
            updateState = { newValue ->
                onFieldUpdate(FormFieldName.FoodLog.TIME, newValue)
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(FormFieldName.FoodLog.TIME, it.text)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
        )
    }
}
