package com.example.fitnessway.feature.profile.manager.goals

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface IGoalsManager {
    val goalsEditionFormState: StateFlow<FormState<FormStates.NutrientGoals>?>
    val modifiedGoals: StateFlow<Map<Int, String>>
    val isGoalsFormValid: StateFlow<Boolean>
    val colorsEditionFormState: StateFlow<FormState<FormStates.NutrientColors>?>

    fun initNutrientGoalsForm(nutrientsData: NutrientsByType<NutrientWithPreferences>)
    fun updateGoalEditionFormField(fieldName: FormFieldName.NutrientGoalData, input: String)
    fun setGoalsThatChanged()
    fun initNutrientColorsForm(nutrientsData: NutrientsByType<NutrientWithPreferences>)
    fun updateColorsEditionFormField(fieldName: FormFieldName.NutrientColorUpdate, input: String)


    fun init(scope: CoroutineScope)
}