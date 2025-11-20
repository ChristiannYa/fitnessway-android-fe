package com.example.fitnessway.feature.lists.manager.edition

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IEditionManager {
    val selectedFood: StateFlow<FoodInformation?>
    val foodEditionFormState: StateFlow<FormState<FormStates.FoodEdition>?>

    fun setSelectedFood(food: FoodInformation)
    fun initializeFoodForm(food: FoodInformation)
    fun updateFoodCreationFormField(
        fieldName: FormFieldName.IFoodEdition,
        input: String
    )
}