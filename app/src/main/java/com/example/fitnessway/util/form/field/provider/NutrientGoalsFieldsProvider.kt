package com.example.fitnessway.util.form.field.provider

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.form.NutrientGoalEditionField
import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates

class NutrientGoalsFieldsProvider(
    private val formState: FormState<FormStates.NutrientGoals>,
    private val onFieldUpdate: (FormFieldName.NutrientGoalData, String) -> Unit
) {
    fun nutrientGoal(nutrientData: NutrientApiFormat): NutrientGoalEditionField {
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