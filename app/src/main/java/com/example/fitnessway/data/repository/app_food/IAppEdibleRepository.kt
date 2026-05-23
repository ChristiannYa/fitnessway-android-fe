package com.example.fitnessway.data.repository.app_food

import com.example.fitnessway.data.model.m_26.EdibleType
import kotlinx.coroutines.flow.StateFlow

interface IAppEdibleRepository {
    val uiState: StateFlow<AppEdibleRepositoryUiState>

    fun findById(id: Int)
    fun findByBarcode(barcode: String)

    fun search(query: String, edibleType: EdibleType)
    fun loadMore(query: String, edibleType: EdibleType)

    fun clear()
}