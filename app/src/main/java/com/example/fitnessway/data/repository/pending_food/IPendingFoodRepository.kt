package com.example.fitnessway.data.repository.pending_food

import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.data.model.m_26.PendingFoodAddRequest
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IPendingFoodRepository {
    val uiState: StateFlow<PendingFoodRepositoryUiState>

    fun loadPendingFoods()
    fun refreshPendingFoods()
    fun loadMorePendingFoods()
    suspend fun addPendingFood(request: PendingFoodAddRequest): Flow<UiState<PendingFood>>
    suspend fun dismissReview(id: Int): Flow<UiState<Unit>>

    fun updateState(update: (PendingFoodRepositoryUiState) -> PendingFoodRepositoryUiState)

    fun clearRepository()
}