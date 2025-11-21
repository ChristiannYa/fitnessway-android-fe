package com.example.fitnessway.feature.lists.manager.edition

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IEditionManager {
    val selectedFood: StateFlow<FoodInformation?>
    val foodEditionFormState: StateFlow<FormState<FormStates.FoodEdition>?>
    val isEditing: StateFlow<Boolean>
    val formNameError: String?
    val formBrandError: String?
    val formAmountPerServingError: String?
    val formServingUnitError: String?
    val areFormNutrientsValid: Boolean
    val isFormValid: Boolean

    fun setSelectedFood(food: FoodInformation)
    fun initializeFoodForm(food: FoodInformation)
    fun updateFoodCreationFormField(fieldName: FormFieldName.IFoodEdition, input: String)
    fun toggleEditionMode()
}