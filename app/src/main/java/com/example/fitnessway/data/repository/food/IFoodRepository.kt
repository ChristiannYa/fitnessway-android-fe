package com.example.fitnessway.data.repository.food

import com.example.fitnessway.data.model.MFood.Api.Req.FoodAddRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodFavoriteStatusUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodLogAddRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodLogUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodSortUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodUpdateRequest
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.data.model.m_26.PendingFoodAddRequest
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IFoodRepository {
    val uiState: StateFlow<FoodRepositoryUiState>

    fun loadPendingFoods()
    fun refreshPendingFoods()
    fun loadMorePendingFoods()
    suspend fun addPendingFood(request: PendingFoodAddRequest): Flow<UiState<PendingFood>>

    fun refreshFoods()
    fun loadFoods()
    suspend fun deleteFood(foodId: Int): Flow<UiState<FoodInformation>>
    suspend fun addFood(request: FoodAddRequest): Flow<UiState<FoodInformation>>
    suspend fun updateFood(request: FoodUpdateRequest): Flow<UiState<FoodInformation>>

    fun refreshFoodSort()
    fun loadFoodSort()
    fun updateFoodSort(request: FoodSortUpdateRequest): Flow<UiState<String>>

    fun updateFoodFavoriteStatus(request: FoodFavoriteStatusUpdateRequest): Flow<UiState<FoodInformation>>

    fun refreshFoodLogs(date: String)
    fun loadFoodLogs(date: String)
    fun clearFoodLogsUiCache()
    suspend fun addFoodLog(request: FoodLogAddRequest, date: String): Flow<UiState<FoodLogData>>
    suspend fun updateFoodLog(request: FoodLogUpdateRequest, date: String): Flow<UiState<FoodLogData>>
    suspend fun deleteFoodLog(foodLogId: Int, date: String): Flow<UiState<FoodLogData>>

    fun updateState(update: (FoodRepositoryUiState) -> FoodRepositoryUiState)

    fun clearRepository()
}