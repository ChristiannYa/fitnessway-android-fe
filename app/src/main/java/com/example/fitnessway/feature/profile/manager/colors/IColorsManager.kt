package com.example.fitnessway.feature.profile.manager.colors

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IColorsManager {
    val colorsEditionFormState: StateFlow<FormState<FormStates.NutrientColors>?>

    fun initNutrientColorsForm(nutrientsData: NutrientsByType<NutrientWithPreferences>)
    fun updateColorsEditionFormField(fieldName: FormFieldName.NutrientColorUpdate, input: String)

}