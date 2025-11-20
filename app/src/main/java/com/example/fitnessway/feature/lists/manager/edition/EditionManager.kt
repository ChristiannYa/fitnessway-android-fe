package com.example.fitnessway.feature.lists.manager.edition

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditionManager : IEditionManager {
    private val _selectedFood = MutableStateFlow<FoodInformation?>(null)
    override val selectedFood: StateFlow<FoodInformation?> = _selectedFood

    private val emptyFoodFormState = FormStates.FoodEdition(
        name = "",
        brand = "",
        amountPerServing = "",
        servingUnit = ""
    )

    private val _foodEditionFormState = MutableStateFlow(emptyFoodFormState)
    override val foodEditionFormState: StateFlow<FormStates.FoodEdition> = _foodEditionFormState

    override fun setSelectedFood(food: FoodInformation) {
        _selectedFood.value = food
    }

    override fun updateFoodCreationFormField(
        fieldName: FormFieldName.IFoodEdition,
        input: String
    ) {
        _foodEditionFormState.value = when (fieldName) {
            is FormFieldName.FoodEdition.DetailField -> {
                when (fieldName) {
                    FormFieldName.FoodEdition.DetailField.NAME -> {
                        _foodEditionFormState.value.copy(name = input)
                    }

                    FormFieldName.FoodEdition.DetailField.BRAND -> {
                        _foodEditionFormState.value.copy(brand = input)
                    }

                    FormFieldName.FoodEdition.DetailField.AMOUNT_PER_SERVING -> {
                        _foodEditionFormState.value.copy(amountPerServing = input)
                    }

                    FormFieldName.FoodEdition.DetailField.SERVING_UNIT -> {
                        _foodEditionFormState.value.copy(servingUnit = input)
                    }
                }
            }

            is FormFieldName.FoodEdition.NutrientField -> {
                val updatedNutrients = _foodEditionFormState.value.nutrients.toMutableMap()

                updatedNutrients[fieldName.nutrient.id] = input
                _foodEditionFormState.value.copy(nutrients = updatedNutrients)
            }
        }
    }

    override fun resetFoodFormState() {
        _foodEditionFormState.value = emptyFoodFormState
    }
}