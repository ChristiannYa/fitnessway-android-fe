package com.example.fitnessway.data.repository.food_log

import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.FoodLogAddRequest
import com.example.fitnessway.data.model.m_26.FoodLogUpdateRequest
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IFoodLogRepository {
    val uiState: StateFlow<FoodLogRepositoryUiState>

    fun refreshFoodLogs(date: String)
    fun loadFoodLogs(date: String)
    fun clearFoodLogsUiCache()
    suspend fun addFoodLog(request: FoodLogAddRequest, date: String): Flow<UiState<FoodLog>>
    suspend fun updateFoodLog(request: FoodLogUpdateRequest, date: String): Flow<UiState<FoodLog>>
    suspend fun deleteFoodLog(foodLogId: Int, date: String): Flow<UiState<FoodLogData>>

    fun updateState(update: (FoodLogRepositoryUiState) -> FoodLogRepositoryUiState)
    fun clearRepository()
}