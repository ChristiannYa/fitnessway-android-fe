package com.example.fitnessway.feature.home.manager.food

import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IFoodManager {
    val selectedFoodLog: StateFlow<FoodLogData?>
    val foodCreationFormState: StateFlow<FormStates.FoodCreation>
    val currentStep: StateFlow<Number>
    val formNameError: String?
    val formBrandError: String?
    val formAmountPerServingError: String?
    val formServingUnitError: String?

    fun setSelectedFoodLog(foodLog: FoodLogData)
    fun updateFoodCreationFormField(fieldName: FormFieldName.IFoodCreation, input: String)
    fun updateStep(step: Number, goesBack: Boolean = true, onExitForm: (() -> Unit)? = null)
    fun isCurrentStepValid(step: Number): Boolean
}
