package com.example.fitnessway.feature.home.manager.food

import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodManager : IFoodManager {
    private val _selectedFoodLog = MutableStateFlow<FoodLogData?>(null)
    override val selectedFoodLog: StateFlow<FoodLogData?> = _selectedFoodLog

    private val emptyFoodCreationFormState = FormStates.FoodCreation(
        name = "",
        brand = "",
        amountPerServing = "",
        servingUnit = ""
    )

    private val _foodCreationFormState = MutableStateFlow(emptyFoodCreationFormState)
    override val foodCreationFormState: StateFlow<FormStates.FoodCreation> = _foodCreationFormState

    private val _currentStep = MutableStateFlow<Number>(1)
    override val currentStep: StateFlow<Number> = _currentStep

    override fun setSelectedFoodLog(foodLog: FoodLogData) {
        _selectedFoodLog.value = foodLog
    }

    override fun updateFoodCreationFormField(
        fieldName: FormFieldName.IFoodCreation,
        input: String
    ) {
        _foodCreationFormState.value = when (fieldName) {
            is FormFieldName.FoodCreation.BaseField -> {
                when (fieldName) {
                    FormFieldName.FoodCreation.BaseField.NAME -> {
                        _foodCreationFormState.value.copy(name = input)
                    }

                    FormFieldName.FoodCreation.BaseField.BRAND -> {
                        _foodCreationFormState.value.copy(brand = input)
                    }

                    FormFieldName.FoodCreation.BaseField.AMOUNT_PER_SERVING -> {
                        _foodCreationFormState.value.copy(amountPerServing = input)
                    }

                    FormFieldName.FoodCreation.BaseField.SERVING_UNIT -> {
                        _foodCreationFormState.value.copy(servingUnit = input)
                    }
                }
            }
            is FormFieldName.FoodCreation.NutrientField -> {
                val updatedNutrients = _foodCreationFormState.value.nutrients.toMutableMap()
                updatedNutrients[fieldName.nutrient.id] = input
                _foodCreationFormState.value.copy(nutrients = updatedNutrients)
            }
        }
    }

    override fun updateStep(
        step: Number,
        goesBack: Boolean,
        onExitForm: (() -> Unit)?
    ) {
        when (step) {
            1 -> if (goesBack) {
                _foodCreationFormState.value = emptyFoodCreationFormState
                onExitForm?.invoke()
            } else _currentStep.value = 2


            2 -> if (goesBack) _currentStep.value = 1 else _currentStep.value = 3
            3 -> if (goesBack) _currentStep.value = 2 else _currentStep.value = 4
            4 -> if (goesBack) _currentStep.value = 3
        }
    }
}