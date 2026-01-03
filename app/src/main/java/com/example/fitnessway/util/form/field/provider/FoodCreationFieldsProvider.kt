package com.example.fitnessway.util.form.field.provider

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.fitnessway.data.model.form.FoodCreationBaseField
import com.example.fitnessway.data.model.form.FoodCreationNutrientField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.form.FormStates

class FoodCreationFieldsProvider(
    private val formState: FormStates.FoodCreation,
    private val onFieldUpdate: (FormFieldName.IFoodCreation, String) -> Unit,
    private val focusManager: FocusManager,
    private val isFormSubmitting: Boolean
) {
    @Composable
    fun name(focusRequester: FocusRequester? = null): FoodCreationBaseField {
        val value = formState.name

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodCreation.BaseField.NAME
        )

        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.NAME,
            label = "Name",
            value = formState.name, // @TODO: Remove this field. Replaced by textFieldValue
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
                    FormFieldName.FoodCreation.BaseField.NAME,
                    newValue
                )
            }, // @TODO: Remove this field. Replaced by updateTextFieldValueState
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.NAME,
                    it.text
                )
            },
            focusRequester = focusRequester
        )
    }

    @Composable
    fun brand(focusRequester: FocusRequester? = null): FoodCreationBaseField {
        val value = formState.brand

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodCreation.BaseField.BRAND
        )

        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.BRAND,
            label = "Brand",
            value = formState.brand,
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
                    FormFieldName.FoodCreation.BaseField.BRAND,
                    newValue
                )
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.BRAND,
                    it.text
                )
            },
            focusRequester = focusRequester
        )
    }

    @Composable
    fun amountPerServing(focusRequester: FocusRequester? = null): FoodCreationBaseField {
        val value = formState.amountPerServing

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodCreation.BaseField.AMOUNT_PER_SERVING
        )

        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.AMOUNT_PER_SERVING,
            label = "Amount Per Serving",
            value = formState.amountPerServing,
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Decimal
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.AMOUNT_PER_SERVING,
                    newValue
                )
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.AMOUNT_PER_SERVING,
                    it.text
                )
            },
            focusRequester = focusRequester
        )
    }

    @Composable
    fun servingUnit(
        errorMessage: String?,
        focusRequester: FocusRequester? = null
    ): FoodCreationBaseField {
        val value = formState.servingUnit

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = FormFieldName.FoodCreation.BaseField.SERVING_UNIT
        )

        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.SERVING_UNIT,
            label = "Serving unit",
            value = formState.servingUnit,
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.SERVING_UNIT,
                    newValue
                )
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.SERVING_UNIT,
                    it.text
                )
            },
            errorMessage = errorMessage,
            focusRequester = focusRequester,
        )
    }

    @Composable
    fun nutrient(
        nutrientWithPreferences: NutrientWithPreferences,
        focusRequester: FocusRequester? = null,
        isLastField: Boolean,
    ): FoodCreationNutrientField {
        val value = formState.nutrients[nutrientWithPreferences.nutrient.id] ?: ""

        val textFieldValue = Ui.InputUi.rememberTextFieldValueWithCursor(
            text = value,
            key = nutrientWithPreferences.nutrient.id
        )

        val nutrient = nutrientWithPreferences.nutrient

        return FoodCreationNutrientField(
            name = FormFieldName.FoodCreation.NutrientField(nutrientWithPreferences),
            label = "${nutrient.name} ${nutrient.unit}",
            value = value,
            textFieldValue = textFieldValue.value,
            enabled = !isFormSubmitting,
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
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodCreation.NutrientField(nutrientWithPreferences),
                    newValue
                )
            },
            updateTextFieldValueState = {
                textFieldValue.value = it
                onFieldUpdate(
                    FormFieldName.FoodCreation.NutrientField(nutrientWithPreferences),
                    it.text
                )
            },
            focusRequester = focusRequester
        )
    }
}