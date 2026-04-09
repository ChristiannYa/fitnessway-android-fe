package com.example.fitnessway.feature.home.manager.foodlog

import com.example.fitnessway.data.model.m_26.FoodInformation
import com.example.fitnessway.data.model.m_26.FoodInformationWithId
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.FoodLogCategory
import com.example.fitnessway.data.model.m_26.FoodLogListFilter
import com.example.fitnessway.data.model.m_26.FoodToLogSearchCriteria
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.FormFieldName
import kotlinx.coroutines.flow.StateFlow

interface IFoodLogManager {
    val foodLogCategory: StateFlow<FoodLogCategory?>
    val foodList: StateFlow<FoodLogListFilter>

    val selectedFoodLog: StateFlow<FoodLog?>

    val searchCriteria: StateFlow<FoodToLogSearchCriteria?>
    val foodToLog: StateFlow<FoodInformationWithId?>

    val selectedFoodLogToRemove: StateFlow<FoodLog?>

    val foodLogEditionFormState: StateFlow<FormState<FormStates.FoodLogEdition>?>
    val foodLogFormState: StateFlow<FormState<FormStates.FoodLog>?>
    val isFoodLogFormValid: Boolean
    val isFoodLogEditionFormValid: Boolean

    fun setFoodLogCategory(categories: FoodLogCategory)

    fun setFoodList(list: FoodLogListFilter)

    fun setSelectedFoodLog(foodLog: FoodLog)

    fun setSearchCriteria(criteria: FoodToLogSearchCriteria)
    fun setFoodToLog(foodToLog: FoodInformationWithId)

    fun setSelectedFoodLogToRemove(foodLog: FoodLog)

    fun initializeFoodLogEditionForm(foodLog: FoodLog)
    fun initializeFoodLogForm(food: FoodInformation, time: String)
    fun updateFoodLogEditionFormField(fieldName: FormFieldName.FoodLogEdition, input: String)
    fun updateFoodLogFormField(fieldName: FormFieldName.FoodLog, input: String)
}