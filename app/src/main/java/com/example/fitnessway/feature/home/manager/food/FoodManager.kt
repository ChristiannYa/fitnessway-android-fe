package com.example.fitnessway.feature.home.manager.food

import com.example.fitnessway.data.model.food.FoodLogData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodManager : IFoodManager {
    private val _selectedFoodLog = MutableStateFlow<FoodLogData?>(null)
    override val selectedFoodLog: StateFlow<FoodLogData?> = _selectedFoodLog

    override fun setSelectedFoodLog(foodLog: FoodLogData) {
        _selectedFoodLog.value = foodLog
    }
}