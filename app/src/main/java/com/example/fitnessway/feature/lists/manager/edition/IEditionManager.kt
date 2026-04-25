package com.example.fitnessway.feature.lists.manager.edition

import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.model.m_26.UserEdible
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.util.nutrient.INutrientDvControls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface IEditionManager : INutrientDvControls {
    val selectedEdible: StateFlow<UserEdible?>
    val edibleEditionFormState: StateFlow<FormState<FormStates.FoodEdition>?>
    val deletedNutrients: StateFlow<List<Int>>
    val addedNutrients: StateFlow<List<MNutrient.Model.Nutrient>>
    val isEdibleEditionFormValid: StateFlow<Boolean>

    fun setSelectedEdible(food: UserEdible)
    fun initializeEdibleForm(food: UserEdible)
    fun updateEdibleEditionFormField(fieldName: FormFieldName.IFoodEdition, input: String)
    fun addNutrientToForm(nutrient: MNutrient.Model.Nutrient)
    fun filterOutNutrientFromForm(nutrient: MNutrient.Model.Nutrient)
    fun resetDeletedNutrients()
    fun resetAddedNutrients()

    fun init(scope: CoroutineScope)
}