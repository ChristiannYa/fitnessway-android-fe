package com.example.fitnessway.feature.home.manager.food

import com.example.fitnessway.data.model.food.FoodLogData
import kotlinx.coroutines.flow.StateFlow

interface IFoodManager {
    val selectedFoodLog: StateFlow<FoodLogData?>

    fun setSelectedFoodLog(foodLog: FoodLogData)
}