package com.example.fitnessway.data.repository.user_food

import com.example.fitnessway.data.model.MFood.Api.Req.FoodAddRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodFavoriteStatusUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodSortUpdateRequest
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

    fun refreshFoodSort()
    fun loadFoodSort()
    fun updateFoodSort(request: FoodSortUpdateRequest): Flow<UiState<String>>

    fun updateFoodFavoriteStatus(request: FoodFavoriteStatusUpdateRequest): Flow<UiState<FoodInformation>>

    fun updateState(update: (FoodRepositoryUiState) -> FoodRepositoryUiState)
    fun clearRepository()
}