package com.example.fitnessway.data.repository.edible_recent_log.food

import kotlinx.coroutines.flow.StateFlow

// @TODO: Refactor with recently logged supplement repository
interface IFoodRecentLogRepository {
    val uiState: StateFlow<FoodRecentLogRepositoryUiState>

    fun refresh()
    fun load()
    fun loadMore()

    fun updateState(update: (FoodRecentLogRepositoryUiState) -> FoodRecentLogRepositoryUiState)
    fun clear()
}