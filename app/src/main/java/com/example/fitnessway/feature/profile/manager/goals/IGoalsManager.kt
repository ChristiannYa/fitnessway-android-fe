package com.example.fitnessway.feature.profile.manager.goals

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.form.NutrientGoalEditionField
import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IGoalsManager {
    val goalsEditionFormState: StateFlow<FormState<FormStates.NutrientGoals>?>
    val modifiedGoals: StateFlow<List<Int>>
    val isGoalsFormValid: Boolean

    fun initNutrientGoalsForm(goalsData: NutrientsByType<NutrientApiFormat>)
    fun updateGoalEditionFormField(fieldName: FormFieldName.NutrientGoalData, input: String)
    fun startFormEdition()
}