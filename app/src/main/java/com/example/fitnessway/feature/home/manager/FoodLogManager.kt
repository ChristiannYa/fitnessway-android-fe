package com.example.fitnessway.feature.home.manager

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogCategories
import com.example.fitnessway.util.Food
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodLogManager {
    private val _foodLogCategory = MutableStateFlow("")
    val foodLogCategory: StateFlow<String> = _foodLogCategory

    private val _selectedFood = MutableStateFlow<FoodInformation?>(null)
    val selectedFood: StateFlow<FoodInformation?> = _selectedFood

    fun setFoodLogCategory(categories: FoodLogCategories) {
        _foodLogCategory.value = when (categories) {
            FoodLogCategories.BREAKFAST -> "breakfast"
            FoodLogCategories.LUNCH -> "lunch"
            FoodLogCategories.DINNER -> "dinner"
            FoodLogCategories.SUPPLEMENT -> "supplement"
        }
    }

    fun setSelectedFood(food: FoodInformation) {
        _selectedFood.value = food
    }
}