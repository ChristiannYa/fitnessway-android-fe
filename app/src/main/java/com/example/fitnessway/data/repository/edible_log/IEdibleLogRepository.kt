package com.example.fitnessway.data.repository.edible_log

import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.FoodLogAddRequest
import com.example.fitnessway.data.model.m_26.FoodLogUpdateRequest
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IEdibleLogRepository {
    val uiState: StateFlow<EdibleLogRepositoryUiState>

    fun refresh(date: String)
    fun load(date: String)
    suspend fun add(request: FoodLogAddRequest, date: String): Flow<UiState<FoodLog>>
    suspend fun update(request: FoodLogUpdateRequest, date: String): Flow<UiState<FoodLog>>
    suspend fun delete(foodLogId: Int, date: String): Flow<UiState<FoodLogData>>

    fun clearMap()
    fun updateState(update: (EdibleLogRepositoryUiState) -> EdibleLogRepositoryUiState)
    fun clear()
}