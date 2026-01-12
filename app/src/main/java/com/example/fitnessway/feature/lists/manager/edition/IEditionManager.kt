package com.example.fitnessway.feature.lists.manager.edition

import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IEditionManager {
    val selectedFood: StateFlow<FoodInformation?>
    val foodEditionFormState: StateFlow<FormState<FormStates.FoodEdition>?>
    val deletedNutrients: StateFlow<List<Int>>
    val isFormValid: Boolean

    fun setSelectedFood(food: FoodInformation)
    fun initializeFoodForm(food: FoodInformation)
    fun updateFoodEditionFormField(fieldName: FormFieldName.IFoodEdition, input: String)
    fun filterNutrientFromForm(nutrientId: Int)
    fun resetDeletedNutrients()
    fun simpleFormCancel()
}