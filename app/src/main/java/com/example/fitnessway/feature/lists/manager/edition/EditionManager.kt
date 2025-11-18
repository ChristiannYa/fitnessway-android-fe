package com.example.fitnessway.feature.lists.manager.edition

import com.example.fitnessway.data.model.food.FoodInformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditionManager : IEditionManager {
    private val _selectedFood = MutableStateFlow<FoodInformation?>(null)
    override val selectedFood: StateFlow<FoodInformation?> = _selectedFood

    override fun setSelectedFood(food: FoodInformation) {
        _selectedFood.value = food
    }
}