package com.example.fitnessway.feature.lists.manager.food

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodManager : IFoodManager {
    private val _foodNutrientsAsPercentages = MutableStateFlow<Map<Int, String>>(emptyMap())
    override val foodNutrientsAsPercentages: StateFlow<Map<Int, String>> = _foodNutrientsAsPercentages

    override fun addNutrientValueToPercentagesMap(nutrientId: Int, value: String) {
        val nutrientToAdd = _foodNutrientsAsPercentages.value.toMutableMap().apply {
            put(nutrientId, value)
        }

        _foodNutrientsAsPercentages.value = nutrientToAdd
    }

    override fun removeNutrientValueFromPercentagesMap(nutrientId: Int) {
        val nutrientToRemove = _foodNutrientsAsPercentages.value.toMutableMap().apply {
            remove(nutrientId)
        }

        _foodNutrientsAsPercentages.value = nutrientToRemove
    }

    override fun resetNutrientValuesFromPercentagesMap() {
        _foodNutrientsAsPercentages.value = emptyMap()
    }
}