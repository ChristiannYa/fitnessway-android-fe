package com.example.fitnessway.data.repository.app_food

import com.example.fitnessway.data.model.m_26.EdibleType
import kotlinx.coroutines.flow.StateFlow

interface IAppFoodRepository {
    val uiState: StateFlow<AppFoodRepositoryUiState>

    fun findAppFoodById(id: Int)

    fun searchAppFoods(query: String, edibleType: EdibleType)
    fun loadMoreAppFoods(query: String, edibleType: EdibleType)
    fun clearAppFoods()
}