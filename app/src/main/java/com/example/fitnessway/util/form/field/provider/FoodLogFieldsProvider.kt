package com.example.fitnessway.util.form.field.provider

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import com.example.fitnessway.data.model.form.FoodLogField
import com.example.fitnessway.data.model.form.FormFieldName
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
        return FoodLogField(
            name = FormFieldName.FoodLog.SERVINGS,
            label = "Servings",
            value = formState.data.servings,
            enabled = !isFormSubmitting,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            updateState = { newValue ->
                onFieldUpdate(FormFieldName.FoodLog.SERVINGS, newValue)
            }
        )
    }

    @Composable
    fun amountPerServing(servingUnit: String): FoodLogField {
        return FoodLogField(
            name = FormFieldName.FoodLog.AMOUNT_PER_SERVING,
            label = "Amount Per Serving ($servingUnit)",
            value = formState.data.amountPerServing,
            enabled = !isFormSubmitting,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            updateState = { newValue ->
                onFieldUpdate(FormFieldName.FoodLog.AMOUNT_PER_SERVING, newValue)
            }
        )
    }

    @Composable
    fun time(): FoodLogField {
        return FoodLogField(
            name = FormFieldName.FoodLog.TIME,
            label = "Time",
            value = formState.data.time,
            enabled = !isFormSubmitting,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            updateState = { newValue ->
                onFieldUpdate(FormFieldName.FoodLog.TIME, newValue)
            }
        )
    }
}
