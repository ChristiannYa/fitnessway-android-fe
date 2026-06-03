package com.example.fitnessway.feature.profile.manager.colors

import com.example.fitnessway.data.model.m_26.NutrientData
import com.example.fitnessway.data.model.m_26.NutrientsByType
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.FormFieldName
import kotlinx.coroutines.flow.StateFlow

interface IColorsManager {
    val colorsEditionFormState: StateFlow<FormState<FormStates.NutrientColors>?>
    val modifiedColors: StateFlow<Map<Int, String>>
    val isColorsFormValid: StateFlow<Boolean>

    fun initNutrientColorsForm(nutrientsData: NutrientsByType<NutrientData>)
    fun updateColorsEditionFormField(fieldName: FormFieldName.NutrientColorUpdate, input: String)
    fun setColorsThatChanged()
}