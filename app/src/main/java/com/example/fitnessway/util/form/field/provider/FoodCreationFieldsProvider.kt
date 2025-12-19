package com.example.fitnessway.util.form.field.provider

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.fitnessway.data.model.form.FoodCreationBaseField
import com.example.fitnessway.data.model.form.FoodCreationNutrientField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.util.form.FormStates

class FoodCreationFieldsProvider(
    private val formState: FormStates.FoodCreation,
    private val onFieldUpdate: (FormFieldName.IFoodCreation, String) -> Unit,
    private val focusManager: FocusManager
) {
    @Composable
    fun name(): FoodCreationBaseField {
        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.NAME,
            label = "Name",
            value = formState.name,
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
            }
        )
    }

    @Composable
    fun brand(): FoodCreationBaseField {
        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.BRAND,
            label = "Brand",
            value = formState.brand,
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
            }
        )
    }

    @Composable
    fun amountPerServing(): FoodCreationBaseField {
        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.AMOUNT_PER_SERVING,
            label = "Amount Per Serving",
            value = formState.amountPerServing,
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
            }
        )
    }

    @Composable
    fun servingUnit(errorMessage: String?): FoodCreationBaseField {
        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.SERVING_UNIT,
            label = "Serving unit",
            value = formState.servingUnit,
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
            errorMessage = errorMessage
        )
    }

    @Composable
    fun nutrient(
        nutrientWithPreferences: NutrientWithPreferences,
        isLastField: Boolean,
    ): FoodCreationNutrientField {
        val n = nutrientWithPreferences.nutrient

        return FoodCreationNutrientField(
            name = FormFieldName.FoodCreation.NutrientField(nutrientWithPreferences),
            label = "${n.name} ${n.unit}",
            value = formState.nutrients[n.id] ?: "",
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
            }
        )
    }
}