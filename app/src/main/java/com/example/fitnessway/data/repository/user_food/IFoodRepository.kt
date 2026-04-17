package com.example.fitnessway.data.repository.user_food

import com.example.fitnessway.data.model.MFood.Api.Req.FoodAddRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodUpdateRequest
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IFoodRepository {
    val uiState: StateFlow<FoodRepositoryUiState>

    fun refreshFoods()
    fun loadFoods()
    suspend fun deleteFood(foodId: Int): Flow<UiState<FoodInformation>>
    suspend fun addFood(request: FoodAddRequest): Flow<UiState<FoodInformation>>
    suspend fun updateFood(request: FoodUpdateRequest): Flow<UiState<FoodInformation>>

    fun updateState(update: (FoodRepositoryUiState) -> FoodRepositoryUiState)
    fun clearRepository()
}