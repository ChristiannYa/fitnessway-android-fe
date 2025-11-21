package com.example.fitnessway.feature.lists.manager.edition

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditionManager : IEditionManager {
    private val _selectedFood = MutableStateFlow<FoodInformation?>(null)
    override val selectedFood: StateFlow<FoodInformation?> = _selectedFood

    private val _foodEditionFormState = MutableStateFlow<FormState<FormStates.FoodEdition>?>(null)
    override val foodEditionFormState: StateFlow<FormState<FormStates.FoodEdition>?> =
        _foodEditionFormState

    private val _isEditing = MutableStateFlow(false)
    override val isEditing: StateFlow<Boolean> = _isEditing

    override fun setSelectedFood(food: FoodInformation) {
        _selectedFood.value = food
    }

    override fun initializeFoodForm(food: FoodInformation) {
        val nutrients = (food.nutrients.basic + food.nutrients.vitamin + food.nutrients.mineral)
            .associate { it.nutrient.id to doubleFormatter(it.amount) }
        // Result: {1="10.5", 2="20.3", 3="15"}
        //
        // If `.map` where to be used instead it would result in:
        // [(1, "10.5"), (2, "20.3"), (3, "15")]
        // which is a `List` but we need a map


        _foodEditionFormState.value = FormState(
            data = FormStates.FoodEdition(
                name = food.information.name,
                brand = food.information.brand ?: "",
                amountPerServing = doubleFormatter(food.information.amountPerServing),
                servingUnit = food.information.servingUnit,
                nutrients = nutrients
            )
        )
    }

    override fun updateFoodCreationFormField(
        fieldName: FormFieldName.IFoodEdition,
        input: String
    ) {
        _foodEditionFormState.value?.let { currentState ->
            val updatedData = when (fieldName) {
                is FormFieldName.FoodEdition.DetailField -> {
                    when (fieldName) {
                        FormFieldName.FoodEdition.DetailField.NAME -> {
                            currentState.data.copy(name = input)
                        }

                        FormFieldName.FoodEdition.DetailField.BRAND -> {
                            currentState.data.copy(brand = input)
                        }

                        FormFieldName.FoodEdition.DetailField.AMOUNT_PER_SERVING -> {
                            currentState.data.copy(amountPerServing = input)
                        }

                        FormFieldName.FoodEdition.DetailField.SERVING_UNIT -> {
                            currentState.data.copy(servingUnit = input)
                        }
                    }
                }

                is FormFieldName.FoodEdition.NutrientField -> {
                    val updatedNutrients = currentState.data.nutrients.toMutableMap()

                    updatedNutrients[fieldName.nutrient.id] = input
                    currentState.data.copy(nutrients = updatedNutrients)
                }
            }

            _foodEditionFormState.value = currentState.copy(data = updatedData)
        }
    }

    override fun toggleEditionMode() {
        _isEditing.value = !isEditing.value
    }
}