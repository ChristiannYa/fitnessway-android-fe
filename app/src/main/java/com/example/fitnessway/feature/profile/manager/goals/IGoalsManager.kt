package com.example.fitnessway.feature.profile.manager.goals

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.MNutrient.Model.NutrientWithPreferences
import com.example.fitnessway.data.model.MNutrient.Model.NutrientsByType
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IGoalsManager {
    val goalsEditionFormState: StateFlow<FormState<FormStates.NutrientGoals>?>
    val modifiedGoals: StateFlow<Map<Int, String>>
    val isGoalsFormValid: StateFlow<Boolean>

    fun initNutrientGoalsForm(nutrientsData: NutrientsByType<NutrientWithPreferences>)
    fun updateGoalEditionFormField(fieldName: FormFieldName.NutrientGoalData, input: String)
    fun setGoalsThatChanged()
}