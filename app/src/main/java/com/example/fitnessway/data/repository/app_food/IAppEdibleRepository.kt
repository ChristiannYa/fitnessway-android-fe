package com.example.fitnessway.data.repository.app_food

import com.example.fitnessway.data.model.m_26.AppEdibleReportRequest
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IAppEdibleRepository {
    val uiState: StateFlow<AppEdibleRepositoryUiState>

    fun findById(id: Int)
    fun findByBarcode(barcode: String)

    fun search(query: String, edibleType: EdibleType)
    fun loadMore(query: String, edibleType: EdibleType)

    fun report(req: AppEdibleReportRequest): Flow<UiState<Unit>>

    fun clear()
}