package com.example.fitnessway.feature.home.manager.foodlog

import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.m_26.FoodInformation
import com.example.fitnessway.data.model.m_26.FoodLogCategories
import com.example.fitnessway.data.model.m_26.FoodSource
import com.example.fitnessway.data.model.m_26.FoodToLogSearchCriteria
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.FormFieldName
import kotlinx.coroutines.flow.StateFlow

interface IFoodLogManager {
    val foodLogCategory: StateFlow<FoodLogCategories?>

    val selectedFoodLog: StateFlow<FoodLogData?>

    val searchCriteria: StateFlow<FoodToLogSearchCriteria?>
    val foodToLog: StateFlow<FoodInformation?>
    val foodInfoToLog: StateFlow<FoodInformation?>
    val foodIdToLog: StateFlow<Int?>
    val foodSourceToLog: StateFlow<FoodSource?>

    val selectedFoodLogToRemove: StateFlow<FoodLogData?>

    val foodLogEditionFormState: StateFlow<FormState<FormStates.FoodLogEdition>?>
    val foodLogFormState: StateFlow<FormState<FormStates.FoodLog>?>
    val isFoodLogFormValid: Boolean
    val isFoodLogEditionFormValid: Boolean

    fun setFoodLogCategory(categories: FoodLogCategories)

    fun setSelectedFoodLog(foodLog: FoodLogData)

    fun setSearchCriteria(foodToLogSearchCriteria: FoodToLogSearchCriteria)
    fun setFoodToLog(foodToLog: FoodInformation)
    fun setFoodInfoToLog(food: FoodInformation)
    fun setFoodIdToLog(id: Int)
    fun setFoodSourceToLog(source: FoodSource)

    fun setSelectedFoodLogToRemove(foodLog: FoodLogData)

    fun initializeFoodLogEditionForm(foodLog: FoodLogData)
    fun initializeFoodLogForm(food: FoodInformation, time: String)
    fun updateFoodLogEditionFormField(fieldName: FormFieldName.FoodLogEdition, input: String)
    fun updateFoodLogFormField(fieldName: FormFieldName.FoodLog, input: String)
}