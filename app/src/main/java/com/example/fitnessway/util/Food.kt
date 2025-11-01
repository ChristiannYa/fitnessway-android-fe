package com.example.fitnessway.util

import com.example.fitnessway.data.model.food.FoodInformation

object Food {
    fun getFoodById(
        foodList: List<FoodInformation>,
        foodId: Int
    ): FoodInformation? {
        return foodList.find { it.information.id == foodId }
    }
}