package com.example.fitnessway.util.form.field.provider

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.fitnessway.data.model.form.FoodLogEditionField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates

class FoodLogEditionFieldsProvider(
    private val formState: FormState<FormStates.FoodLogEdition>,
    private val onFieldUpdate: (FormFieldName.FoodLogEdition, String) -> Unit,
    private val focusManager: FocusManager
) {
    @Composable
    fun servings(): FoodLogEditionField {
        val value = formState.data.servings

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodLogEdition.SERVINGS
        )

        return FoodLogEditionField(
            name = FormFieldName.FoodLogEdition.SERVINGS,
            label = "Servings",
            value = formState.data.servings,
            textFieldValue = textFieldValue.value,
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodLogEdition.SERVINGS,
                    newValue
                )
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.FoodLogEdition.SERVINGS,
                    it.text
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
        )
    }

    @Composable
    fun amountPerServing(servingUnit: String): FoodLogEditionField {
        val value = formState.data.amountPerServing

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodLogEdition.AMOUNT_PER_SERVING
        )

        return FoodLogEditionField(
            name = FormFieldName.FoodLogEdition.AMOUNT_PER_SERVING,
            label = "Amount Per Serving ($servingUnit)",
            value = formState.data.amountPerServing,
            textFieldValue = textFieldValue.value,
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodLogEdition.AMOUNT_PER_SERVING,
                    newValue
                )
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.FoodLogEdition.AMOUNT_PER_SERVING,
                    it.text
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
        )
    }
}