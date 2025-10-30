package com.example.fitnessway.data.repository.food

import com.example.fitnessway.data.model.food.FoodLogsByCategory
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface IFoodRepository {
    suspend fun getFoodLogs(date: String): Flow<UiState<FoodLogsByCategory>>
}