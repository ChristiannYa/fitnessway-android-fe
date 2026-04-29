package com.example.fitnessway.data.repository.pending

import com.example.fitnessway.data.model.m_26.EdibleAddRequest
import com.example.fitnessway.data.repository.IRepository
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface IPendingEdibleRepository<T> : IRepository<T> {
    fun refresh()
    fun load()
    fun loadMore()

    suspend fun add(request: EdibleAddRequest): Flow<UiState<Unit>>
    suspend fun dismiss(id: Int): Flow<UiState<Unit>>
}