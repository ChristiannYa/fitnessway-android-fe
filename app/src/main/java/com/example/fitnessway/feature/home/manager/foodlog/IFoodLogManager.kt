package com.example.fitnessway.feature.home.manager.foodlog

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogCategories
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IFoodLogManager {
    val foodLogCategory: StateFlow<String>
    val selectedFood: StateFlow<FoodInformation?>
    val foodLogFormState: StateFlow<FormState<FormStates.FoodLog>?> // Null until the user selects a food
    val isFoodLogFormValid: Boolean
    val shouldShowFoodLogSuccess: Boolean

    fun setFoodLogCategory(categories: FoodLogCategories)
    fun setSelectedFood(food: FoodInformation)
    fun initializeFoodLogForm(food: FoodInformation, time: String)
    fun updateFoodLogFormField(fieldName: FormFieldName.FoodLog, input: String)
    fun startFoodLogEdit()
    fun cancelFoodLogEdit()
    fun saveFoodLogEdit()
}