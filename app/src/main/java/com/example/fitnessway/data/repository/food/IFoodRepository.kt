package com.example.fitnessway.data.repository.food

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogAddRequest
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.food.FoodLogsByCategory
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface IFoodRepository {
    suspend fun getFoods(): Flow<UiState<List<FoodInformation>>>
    suspend fun getFoodLogs(date: String): Flow<UiState<FoodLogsByCategory>>

    suspend fun addFoodLog(
        request: FoodLogAddRequest,
        date: String
    ): Flow<UiState<FoodLogData>>

    suspend fun deleteFoodLog(
        foodLogId: Int,
        date: String
    ): Flow<UiState<FoodLogData>>
}