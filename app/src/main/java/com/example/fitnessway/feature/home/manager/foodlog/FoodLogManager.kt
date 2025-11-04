package com.example.fitnessway.feature.home.manager.foodlog

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogCategories
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodLogManager : IFoodLogManager {
    private val _foodLogCategory = MutableStateFlow("")
    override val foodLogCategory: StateFlow<String> = _foodLogCategory

    private val _selectedFood = MutableStateFlow<FoodInformation?>(null)
    override val selectedFood: StateFlow<FoodInformation?> = _selectedFood

    private val _foodLogFormState = MutableStateFlow<FormState<FormStates.FoodLog>?>(null)
    override val foodLogFormState: StateFlow<FormState<FormStates.FoodLog>?> = _foodLogFormState

    override fun setFoodLogCategory(categories: FoodLogCategories) {
        _foodLogCategory.value = when (categories) {
            FoodLogCategories.BREAKFAST -> "breakfast"
            FoodLogCategories.LUNCH -> "lunch"
            FoodLogCategories.DINNER -> "dinner"
            FoodLogCategories.SUPPLEMENT -> "supplement"
        }
    }

    override fun setSelectedFood(food: FoodInformation) {
        _selectedFood.value = food
    }

    override fun initializeFoodLogForm(food: FoodInformation, time: String) {
        _foodLogFormState.value = FormState(
            data = FormStates.FoodLog(
                servings = 1.0,
                amountPerServing = food.information.amountPerServing,
                time = time
            )
        )
    }

    override fun updateFoodLogFormField(
        fieldName: FormFieldName.FoodLog,
        input: String
    ) {
        _foodLogFormState.value?.let { currentState ->
            val updatedData = when (fieldName) {
                FormFieldName.FoodLog.SERVINGS -> {
                    currentState.data.copy(
                        servings = input.toDoubleOrNull() ?: currentState.data.servings
                    )
                }

                FormFieldName.FoodLog.AMOUNT_PER_SERVING -> {
                    currentState.data.copy(
                        amountPerServing = input.toDoubleOrNull()
                            ?: currentState.data.amountPerServing
                    )
                }

                FormFieldName.FoodLog.TIME -> {
                    currentState.data.copy(time = input)
                }
            }

            _foodLogFormState.value = currentState.copy(data = updatedData)
        }
    }

    override fun startFoodLogEdit() {
        _foodLogFormState.value = _foodLogFormState.value?.edit()
    }

    override fun cancelFoodLogEdit() {
        _foodLogFormState.value = _foodLogFormState.value?.cancel()
    }

    override fun saveFoodLogEdit() {
        _foodLogFormState.value = _foodLogFormState.value?.save()
    }
}