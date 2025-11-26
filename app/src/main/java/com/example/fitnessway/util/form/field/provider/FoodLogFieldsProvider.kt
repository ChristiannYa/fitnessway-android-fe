package com.example.fitnessway.util.form.field.provider

import androidx.compose.runtime.Composable
import com.example.fitnessway.data.model.form.FoodLogField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.Formatters.doubleFormatter

class FoodLogFieldsProvider(
    private val formState: FormState<FormStates.FoodLog>,
    private val onFieldUpdate: (FormFieldName.FoodLog, String) -> Unit
) {
    val isEditing = formState.isEditing

    @Composable
    fun servings(): FoodLogField {
        val value = if (isEditing) {
            formState.data.servings
        } else doubleFormatter(
            value = (formState.data.servings).toDouble(),
            decimalPlaces = 2
        )

        return FoodLogField(
            name = FormFieldName.FoodLog.SERVINGS,
            label = "Servings",
            value = value,
            updateState = { newValue ->
                onFieldUpdate(FormFieldName.FoodLog.SERVINGS, newValue)
            }
        )
    }

    @Composable
    fun amountPerServing(servingUnit: String): FoodLogField {
        val value = if (isEditing) {
            formState.data.amountPerServing
        } else doubleFormatter(
            value = (formState.data.amountPerServing).toDouble(),
            decimalPlaces = 2
        )

        return FoodLogField(
            name = FormFieldName.FoodLog.AMOUNT_PER_SERVING,
            label = "Amount Per Serving ($servingUnit)",
            value = value,
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
            updateState = { newValue ->
                onFieldUpdate(FormFieldName.FoodLog.TIME, newValue)
            }
        )
    }
}
