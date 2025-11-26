package com.example.fitnessway.util.form.field.provider

import androidx.compose.runtime.Composable
import com.example.fitnessway.data.model.form.FoodLogEditionField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates

class FoodLogEditionFieldsProvider(
    private val formState: FormState<FormStates.FoodLogEdition>,
    private val onFieldUpdate: (FormFieldName.FoodLogEdition, String) -> Unit
) {
    @Composable
    fun servings(): FoodLogEditionField {
        val value = if (formState.isEditing) {
            formState.data.servings
        } else doubleFormatter(
            value = (formState.data.servings).toDouble(),
            decimalPlaces = 2
        )

        return FoodLogEditionField(
            name = FormFieldName.FoodLogEdition.SERVINGS,
            label = "Servings",
            value = value,
            updateState = { newValue ->
                onFieldUpdate(FormFieldName.FoodLogEdition.SERVINGS, newValue)
            }
        )
    }

    @Composable
    fun amountPerServing(servingUnit: String): FoodLogEditionField {
        val value = if (formState.isEditing) {
            formState.data.amountPerServing
        } else doubleFormatter(
            value = (formState.data.amountPerServing).toDouble(),
            decimalPlaces = 2
        )

        return FoodLogEditionField(
            name = FormFieldName.FoodLogEdition.AMOUNT_PER_SERVING,
            label = "Amount Per Serving ($servingUnit)",
            value = value,
            updateState = { newValue ->
                onFieldUpdate(FormFieldName.FoodLogEdition.AMOUNT_PER_SERVING, newValue)
            }
        )
    }
}