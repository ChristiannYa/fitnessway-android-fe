package com.example.fitnessway.feature.home.manager.foodlog

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogCategories
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IFoodLogManager {
    val foodLogCategory: StateFlow<String>
    val selectedFoodLog: StateFlow<FoodLogData?>
    val selectedFoodToLog: StateFlow<FoodInformation?>
    val selectedFoodLogToRemove: StateFlow<FoodLogData?>
    val foodLogEditionFormState: StateFlow<FormState<FormStates.FoodLogEdition>?>
    val foodLogFormState: StateFlow<FormState<FormStates.FoodLog>?>
    val isFoodLogFormValid: Boolean

    fun setFoodLogCategory(categories: FoodLogCategories)
    fun setSelectedFoodLog(foodLog: FoodLogData)
    fun setSelectedFoodToLog(food: FoodInformation)
    fun setSelectedFoodLogToRemove(foodLog: FoodLogData)
    fun initializeFoodLogEditionForm(foodLog: FoodLogData)
    fun initializeFoodLogForm(food: FoodInformation, time: String)
    fun updateFoodLogEditionFormField(fieldName: FormFieldName.FoodLogEdition, input: String)
    fun updateFoodLogFormField(fieldName: FormFieldName.FoodLog, input: String)
    fun startFormEdit(formState: FormStates)
    fun cancelFormEdit(formState: FormStates)
    fun saveFormEdit(formState: FormStates)
}