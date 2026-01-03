package com.example.fitnessway.util.form.field.provider

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.fitnessway.data.model.form.FoodEditionDetailField
import com.example.fitnessway.data.model.form.FoodEditionNutrientField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates

class FoodEditionFieldsProvider(
    private val formState: FormState<FormStates.FoodEdition>,
    private val onFieldUpdate: (FormFieldName.IFoodEdition, String) -> Unit,
    private val focusManager: FocusManager,
    private val isFormSubmitting: Boolean
) {
    @Composable
    fun name(): FoodEditionDetailField {
        val value = formState.data.name

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodEdition.DetailField.NAME
        )

        return FoodEditionDetailField(
            name = FormFieldName.FoodEdition.DetailField.NAME,
            label = "Name",
            value = value,
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodEdition.DetailField.NAME,
                    newValue
                )
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.FoodEdition.DetailField.NAME,
                    it.text
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
        )
    }

    @Composable
    fun brand(): FoodEditionDetailField {
        val value = formState.data.brand

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodEdition
        )

        return FoodEditionDetailField(
            name = FormFieldName.FoodEdition.DetailField.BRAND,
            label = "Brand",
            value = formState.data.brand,
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodEdition.DetailField.BRAND,
                    newValue
                )
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.FoodEdition.DetailField.BRAND,
                    it.text
                )
            }
        )
    }

    @Composable
    fun amountPerServing(): FoodEditionDetailField {
        val value = formState.data.amountPerServing

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodEdition.DetailField.AMOUNT_PER_SERVING
        )

        return FoodEditionDetailField(
            name = FormFieldName.FoodEdition.DetailField.AMOUNT_PER_SERVING,
            label = "Amount Per Serving",
            value = formState.data.amountPerServing,
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodEdition.DetailField.AMOUNT_PER_SERVING,
                    newValue
                )
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.FoodEdition.DetailField.AMOUNT_PER_SERVING,
                    it.text
                )
            }
        )
    }

    @Composable
    fun servingUnit(): FoodEditionDetailField {
        val value = formState.data.servingUnit

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodEdition.DetailField.SERVING_UNIT
        )

        return FoodEditionDetailField(
            name = FormFieldName.FoodEdition.DetailField.SERVING_UNIT,
            label = "Serving Unit",
            value = formState.data.servingUnit,
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodEdition.DetailField.SERVING_UNIT,
                    newValue
                )
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.FoodEdition.DetailField.SERVING_UNIT,
                    it.text
                )
            }
        )
    }

    @Composable
    fun nutrient(
        nutrient: Nutrient,
        isLastField: Boolean
    ): FoodEditionNutrientField {
        val value = formState.data.nutrients[nutrient.id] ?: ""

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = nutrient.id
        )

        return FoodEditionNutrientField(
            name = FormFieldName.FoodEdition.NutrientField(nutrient),
            label = "${nutrient.name} ${nutrient.unit}",
            value = value, // @TODO: Remove this field. Replaced by textFieldValue
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodEdition.NutrientField(nutrient),
                    newValue
                )
            }, // @TODO: Remove this field. Replaced by updateTextFieldValueState
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.FoodEdition.NutrientField(nutrient),
                    it.text // extract the text from the state
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