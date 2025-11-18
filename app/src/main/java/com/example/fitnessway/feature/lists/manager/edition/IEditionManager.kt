package com.example.fitnessway.feature.lists.manager.edition

import com.example.fitnessway.data.model.food.FoodInformation
import kotlinx.coroutines.flow.StateFlow

interface IEditionManager {
    val selectedFood: StateFlow<FoodInformation?>

    fun setSelectedFood(food: FoodInformation)
}