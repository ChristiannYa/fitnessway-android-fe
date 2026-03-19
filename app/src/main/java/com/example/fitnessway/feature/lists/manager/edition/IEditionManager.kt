package com.example.fitnessway.feature.lists.manager.edition

import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.FormFieldName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface IEditionManager {
    val selectedFood: StateFlow<FoodInformation?>
    val foodEditionFormState: StateFlow<FormState<FormStates.FoodEdition>?>
    val deletedNutrients: StateFlow<List<Int>>
    val addedNutrients: StateFlow<List<MNutrient.Model.Nutrient>>
    val isFoodEditionFormValid: StateFlow<Boolean>

    fun setSelectedFood(food: FoodInformation)
    fun initializeFoodForm(food: FoodInformation)
    fun updateFoodEditionFormField(fieldName: FormFieldName.IFoodEdition, input: String)
    fun addNutrientToForm(nutrient: MNutrient.Model.Nutrient)
    fun filterOutNutrientFromForm(nutrient: MNutrient.Model.Nutrient)
    fun resetDeletedNutrients()
    fun resetAddedNutrients()

    fun init(scope: CoroutineScope)
}