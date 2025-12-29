package com.example.fitnessway.util.form.field.provider

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.form.NutrientGoalEditionField
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates

class NutrientGoalsFieldsProvider(
    private val formState: FormState<FormStates.NutrientGoals>,
    private val onFieldUpdate: (FormFieldName.NutrientGoalData, String) -> Unit,
    private val isFormSubmitting: Boolean
) {
    fun nutrientGoal(nutrientData: NutrientWithPreferences): NutrientGoalEditionField {
        val nutrient = nutrientData.nutrient

        return NutrientGoalEditionField(
            name = FormFieldName.NutrientGoalData(nutrientData),
            label = "${nutrient.name} ${nutrient.unit}",
            value = formState.data.goals[nutrient.id] ?: "~",
            enabled = !isFormSubmitting,
            updateState = { value ->
                onFieldUpdate(
                    FormFieldName.NutrientGoalData(nutrientData),
                    value
                )
            }
        )
    }
}