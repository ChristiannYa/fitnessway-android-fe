package com.example.fitnessway.feature.home.manager.foodlog

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogCategories
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.Formatters.doubleFormatter
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

    override val isFoodLogFormValid: Boolean
        get() = foodLogFormState.value?.data?.let { data ->
            val servings = data.servings.toDoubleOrNull()
            val amountPerServing = data.amountPerServing.toDoubleOrNull()

            servings != null && servings > 0 &&
                    amountPerServing != null && amountPerServing > 0
        } ?: false

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
                servings = doubleFormatter(1.0),
                amountPerServing = doubleFormatter(food.information.amountPerServing),
                amountPerServingDb = food.information.amountPerServing,
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
                    val newAmount = input.toDoubleOrNull()

                    val dynAmountPerServing = if (newAmount != null && newAmount > 0) {
                        val amount = currentState.data.amountPerServingDb * newAmount
                        doubleFormatter(amount)
                    } else currentState.data.amountPerServing // Keep current if invalid

                    currentState.data.copy(
                        servings = input,
                        amountPerServing = dynAmountPerServing
                    )
                }

                FormFieldName.FoodLog.AMOUNT_PER_SERVING -> {
                    val newAmount = input.toDoubleOrNull()

                    val dynServings = if (newAmount != null && newAmount > 0) {
                        val amount = newAmount / currentState.data.amountPerServingDb
                        doubleFormatter(amount)
                    } else currentState.data.servings // Keep current if invalid

                    currentState.data.copy(
                        amountPerServing = input,
                        servings = dynServings
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