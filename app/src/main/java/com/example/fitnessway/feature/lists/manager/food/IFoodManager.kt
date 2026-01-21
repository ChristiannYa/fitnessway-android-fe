package com.example.fitnessway.feature.lists.manager.food

import kotlinx.coroutines.flow.StateFlow

interface IFoodManager {
    val foodNutrientsAsPercentages: StateFlow<Map<Int, String>>

    fun addNutrientValueToPercentagesMap(nutrientId: Int, value: String)
    fun removeNutrientValueFromPercentagesMap(nutrientId: Int)
    fun resetNutrientValuesFromPercentagesMap()
}