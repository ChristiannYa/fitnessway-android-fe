package com.example.fitnessway.util.form.providers

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
    @Composable
    fun Servings(): FoodLogField {
        return FoodLogField(
            name = FormFieldName.FoodLog.SERVINGS,
            label = "Servings",
            value = doubleFormatter(formState.data.servings),

            // @NOTE
            // By providing the `newValue` lambda, we satisfy the (String) -> Unit requirement
            // that `updateState` needs
            updateState = { newValue ->
                onFieldUpdate(FormFieldName.FoodLog.SERVINGS, newValue)
            } // This whole code block results in `(String) -> Unit`

            // ```
            // fun updateState(newValue: String) {
            //     onFieldUpdate(FormFieldName.FoodLog.SERVINGS, newValue)
            // }
            // ```
        )
    }

    @Composable
    fun AmountPerServing(servingUnit: String): FoodLogField {
        return FoodLogField(
            name = FormFieldName.FoodLog.AMOUNT_PER_SERVING,
            label = "Amount Per Serving ($servingUnit)",
            value = doubleFormatter(formState.data.amountPerServing),
            updateState = { newValue ->
                onFieldUpdate(FormFieldName.FoodLog.AMOUNT_PER_SERVING, newValue)
            }
        )
    }

    @Composable
    fun Time(): FoodLogField {
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
