package com.example.fitnessway.data.repository.edible_log

import com.example.fitnessway.data.model.m_26.EdibleLogAddRequest
import com.example.fitnessway.data.model.m_26.FoodLogUpdateRequest
import com.example.fitnessway.data.repository.IRepository
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface IEdibleLogRepository : IRepository<EdibleLogRepositoryUiState> {
    fun setDate(date: String)

    fun load()

    suspend fun add(request: EdibleLogAddRequest, date: String): Flow<UiState<Unit>>
    suspend fun update(request: FoodLogUpdateRequest, date: String): Flow<UiState<Unit>>
    suspend fun delete(foodLogId: Int, date: String): Flow<UiState<Unit>>
}