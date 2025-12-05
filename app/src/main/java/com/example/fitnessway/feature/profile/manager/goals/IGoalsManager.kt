package com.example.fitnessway.feature.profile.manager.goals

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IGoalsManager {
    val goalsEditionState: StateFlow<FormState<FormStates.NutrientGoals>?>
    val modifiedGoals: StateFlow<List<Int>>

    fun updateGoalEditionFormField(fieldName: FormFieldName.NutrientGoalData, input: String)
    fun addGoalToModifiedList(nutrientId: Int)
    fun removeGoalFromModifierList(nutrientId: Int)
    fun startFormEdition()
}