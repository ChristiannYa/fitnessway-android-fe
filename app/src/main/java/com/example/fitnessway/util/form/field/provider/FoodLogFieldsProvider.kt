package com.example.fitnessway.util.form.field.provider

import androidx.compose.runtime.Composable
import com.example.fitnessway.data.model.form.FoodLogField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates

class FoodLogFieldsProvider(
    private val formState: FormState<FormStates.FoodLog>,
    private val onFieldUpdate: (FormFieldName.FoodLog, String) -> Unit
) {
    @Composable
    fun servings(): FoodLogField {
        return FoodLogField(
            name = FormFieldName.FoodLog.SERVINGS,
            label = "Servings",
            value = formState.data.servings,
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
