package com.example.fitnessway.feature.lists.manager.food

import kotlinx.coroutines.flow.MutableStateFlow

class FoodManager : IFoodManager {
    private val _foodNutrientsAsPercentages = MutableStateFlow<Map<Int, String>>(emptyMap())

    private fun addNutrientValueToPercentagesMap(nutrientId: Int, value: String) {
        val nutrientToAdd = _foodNutrientsAsPercentages.value.toMutableMap().apply {
            put(nutrientId, value)
        }

        _foodNutrientsAsPercentages.value = nutrientToAdd
    }

    private fun removeNutrientValueFromPercentagesMap(nutrientId: Int) {
        val nutrientToRemove = _foodNutrientsAsPercentages.value.toMutableMap().apply {
            remove(nutrientId)
        }

        _foodNutrientsAsPercentages.value = nutrientToRemove
    }

    private fun resetNutrientPercentageConfigData() {
        _foodNutrientsAsPercentages.value = emptyMap()
    }

    override val nutrientDvControls = IFoodManager.NutrientDvControls(
        nutrientDvMap = _foodNutrientsAsPercentages,
        onAdd = ::addNutrientValueToPercentagesMap,
        onRemove = ::removeNutrientValueFromPercentagesMap,
        onClearData = ::resetNutrientPercentageConfigData
    )
}