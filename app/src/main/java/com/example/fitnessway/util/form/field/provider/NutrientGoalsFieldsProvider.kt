package com.example.fitnessway.util.form.field.provider

import androidx.compose.runtime.Composable
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.form.NutrientGoalEditionField
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates

class NutrientGoalsFieldsProvider(
    private val formState: FormState<FormStates.NutrientGoals>,
    private val onFieldUpdate: (FormFieldName.NutrientGoalData, String) -> Unit
) {
    // @TODO: Make the label a Text Composable instead of just a String
    @Composable
    fun nutrientGoal(nutrientData: NutrientAmountData): NutrientGoalEditionField {
        val nutrient = nutrientData.nutrient

        return NutrientGoalEditionField(
            name = FormFieldName.NutrientGoalData(nutrientData),
            label = "${nutrient.name} ${nutrient.unit}",
            value = formState.data.goals[nutrient.id] ?: "~",
            updateState = { value ->
                onFieldUpdate(
                    FormFieldName.NutrientGoalData(nutrientData),
                    value
                )
            }
        )
    }
}