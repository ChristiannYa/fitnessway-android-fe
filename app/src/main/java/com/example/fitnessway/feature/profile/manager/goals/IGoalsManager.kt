package com.example.fitnessway.feature.profile.manager.goals

import com.example.fitnessway.data.model.m_26.NutrientData
import com.example.fitnessway.data.model.m_26.NutrientsByType
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.FormFieldName
import kotlinx.coroutines.flow.StateFlow

interface IGoalsManager {
    val goalsEditionFormState: StateFlow<FormState<FormStates.NutrientGoals>?>
    val modifiedGoals: StateFlow<Map<Int, String>>
    val isGoalsFormValid: StateFlow<Boolean>

    fun initNutrientGoalsForm(nutrientsData: NutrientsByType<NutrientData>)
    fun updateGoalEditionFormField(fieldName: FormFieldName.NutrientGoalData, input: String)
    fun setGoalsThatChanged()
}