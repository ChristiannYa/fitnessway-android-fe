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
    val selectedFood: StateFlow<UserEdible?>
    val foodEditionFormState: StateFlow<FormState<FormStates.FoodEdition>?>
    val deletedNutrients: StateFlow<List<Int>>
    val addedNutrients: StateFlow<List<MNutrient.Model.Nutrient>>
    val isFoodEditionFormValid: StateFlow<Boolean>

    fun setSelectedFood(food: UserEdible)
    fun initializeFoodForm(food: UserEdible)
    fun updateFoodEditionFormField(fieldName: FormFieldName.IFoodEdition, input: String)
    fun addNutrientToForm(nutrient: MNutrient.Model.Nutrient)
    fun filterOutNutrientFromForm(nutrient: MNutrient.Model.Nutrient)
    fun resetDeletedNutrients()
    fun resetAddedNutrients()

    fun init(scope: CoroutineScope)
}