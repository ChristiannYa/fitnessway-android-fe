package com.example.fitnessway.feature.profile.manager.colors

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.MNutrient.Model.NutrientWithPreferences
import com.example.fitnessway.data.model.MNutrient.Model.NutrientsByType
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IColorsManager {
    val colorsEditionFormState: StateFlow<FormState<FormStates.NutrientColors>?>
    val modifiedColors: StateFlow<Map<Int, String>>
    val isColorsFormValid: StateFlow<Boolean>

    fun initNutrientColorsForm(nutrientsData: NutrientsByType<NutrientWithPreferences>)
    fun updateColorsEditionFormField(fieldName: FormFieldName.NutrientColorUpdate, input: String)
    fun setColorsThatChanged()
}