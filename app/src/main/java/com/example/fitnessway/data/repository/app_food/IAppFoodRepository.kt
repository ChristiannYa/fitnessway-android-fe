package com.example.fitnessway.data.repository.app_food

import kotlinx.coroutines.flow.StateFlow

interface IAppFoodRepository {
    val uiState: StateFlow<AppFoodRepositoryUiState>

    fun findAppFoodById(id: Int)

    fun searchAppFoods(query: String)
    fun loadMoreAppFoods(query: String)
    fun clearAppFoods()
}