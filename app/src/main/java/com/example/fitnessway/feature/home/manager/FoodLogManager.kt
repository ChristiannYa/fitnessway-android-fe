package com.example.fitnessway.feature.home.manager

import com.example.fitnessway.data.model.food.FoodLogCategories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodLogManager {
    private val _foodLogCategory = MutableStateFlow("")
    val foodLogCategory: StateFlow<String> = _foodLogCategory

    fun setFoodLogCategory(categories: FoodLogCategories) {
        _foodLogCategory.value = when (categories) {
            FoodLogCategories.BREAKFAST -> "breakfast"
            FoodLogCategories.LUNCH -> "lunch"
            FoodLogCategories.DINNER -> "dinner"
            FoodLogCategories.SUPPLEMENT -> "supplement"
        }
    }
}