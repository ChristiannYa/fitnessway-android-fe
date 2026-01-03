package com.example.fitnessway.util.form.field.provider

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.example.fitnessway.data.model.form.FoodCreationBaseField
import com.example.fitnessway.data.model.form.FoodCreationNutrientField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.util.form.FormStates

class FoodCreationFieldsProvider(
    private val formState: FormStates.FoodCreation,
    private val onFieldUpdate: (FormFieldName.IFoodCreation, String) -> Unit,
    private val focusManager: FocusManager,
    private val isFormSubmitting: Boolean
) {
    fun name(focusRequester: FocusRequester? = null): FoodCreationBaseField {
        val value = formState.name

        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.NAME,
            label = "Name",
            value = formState.name, // @TODO: Remove this field. Replaced by textFieldValue
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            ),
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
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.NAME,
                    it.text
                )
            },
            focusRequester = focusRequester
        )
    }

    fun brand(focusRequester: FocusRequester? = null): FoodCreationBaseField {
        val value = formState.brand

        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.BRAND,
            label = "Brand",
            value = formState.brand,
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            ),
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
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.BRAND,
                    it.text
                )
            },
            focusRequester = focusRequester
        )
    }

    fun amountPerServing(focusRequester: FocusRequester? = null): FoodCreationBaseField {
        val value = formState.amountPerServing

        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.AMOUNT_PER_SERVING,
            label = "Amount Per Serving",
            value = formState.amountPerServing,
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            ),
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
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.AMOUNT_PER_SERVING,
                    it.text
                )
            },
            focusRequester = focusRequester
        )
    }

    fun servingUnit(
        errorMessage: String?,
        focusRequester: FocusRequester? = null
    ): FoodCreationBaseField {
        val value = formState.servingUnit

        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.SERVING_UNIT,
            label = "Serving unit",
            value = formState.servingUnit,
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            ),
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
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.SERVING_UNIT,
                    it.text
                )
            },
            errorMessage = errorMessage,
            focusRequester = focusRequester,
        )
    }

    fun nutrient(
        nutrientWithPreferences: NutrientWithPreferences,
        focusRequester: FocusRequester? = null,
        isLastField: Boolean,
    ): FoodCreationNutrientField {
        val value = formState.nutrients[nutrientWithPreferences.nutrient.id] ?: ""
        val n = nutrientWithPreferences.nutrient

        return FoodCreationNutrientField(
            name = FormFieldName.FoodCreation.NutrientField(nutrientWithPreferences),
            label = "${n.name} ${n.unit}",
            value = value,
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            ),
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
                onFieldUpdate(
                    FormFieldName.FoodCreation.NutrientField(nutrientWithPreferences),
                    it.text
                )
            },
            focusRequester = focusRequester
        )
    }
}