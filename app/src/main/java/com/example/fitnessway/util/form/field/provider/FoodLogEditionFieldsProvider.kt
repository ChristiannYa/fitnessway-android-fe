package com.example.fitnessway.util.form.field.provider

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import com.example.fitnessway.data.model.form.FoodLogEditionField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates

class FoodLogEditionFieldsProvider(
    private val formState: FormState<FormStates.FoodLogEdition>,
    private val onFieldUpdate: (FormFieldName.FoodLogEdition, String) -> Unit,
    private val focusManager: FocusManager
) {
    @Composable
    fun servings(): FoodLogEditionField {
        return FoodLogEditionField(
            name = FormFieldName.FoodLogEdition.SERVINGS,
            label = "Servings",
            value = formState.data.servings,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodLogEdition.SERVINGS,
                    newValue
                )
            }
        )
    }

    @Composable
    fun amountPerServing(servingUnit: String): FoodLogEditionField {
        return FoodLogEditionField(
            name = FormFieldName.FoodLogEdition.AMOUNT_PER_SERVING,
            label = "Amount Per Serving ($servingUnit)",
            value = formState.data.amountPerServing,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodLogEdition.AMOUNT_PER_SERVING,
                    newValue
                )
            }
        )
    }
}