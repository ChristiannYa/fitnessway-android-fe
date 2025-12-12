package com.example.fitnessway.data.repository.food

import com.example.fitnessway.data.model.food.FoodAddRequest
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogAddRequest
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.food.FoodLogUpdateRequest
import com.example.fitnessway.data.model.food.FoodUpdateRequest
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IFoodRepository {
    val uiState: StateFlow<FoodRepositoryUiState>

    fun loadFoods()
    suspend fun deleteFood(foodId: Int): Flow<UiState<FoodInformation>>
    suspend fun addFood(request: FoodAddRequest): Flow<UiState<FoodInformation>>
    suspend fun updateFood(request: FoodUpdateRequest): Flow<UiState<FoodInformation>>

    fun loadFoodLogs(date: String)
    suspend fun addFoodLog(request: FoodLogAddRequest, date: String): Flow<UiState<FoodLogData>>
    suspend fun updateFoodLog(request: FoodLogUpdateRequest, date: String): Flow<UiState<FoodLogData>>
    suspend fun deleteFoodLog(foodLogId: Int, date: String): Flow<UiState<FoodLogData>>

    fun updateState(update: (FoodRepositoryUiState) -> FoodRepositoryUiState)
}