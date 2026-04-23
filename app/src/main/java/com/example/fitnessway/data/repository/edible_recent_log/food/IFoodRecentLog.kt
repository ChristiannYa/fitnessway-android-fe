package com.example.fitnessway.data.repository.edible_recent_log.food

import kotlinx.coroutines.flow.StateFlow

interface IFoodRecentLog {
    val uiState: StateFlow<FoodRecentLogUiState>

    fun refresh()
    fun load()
    fun loadMore()

    fun updateState(update: (FoodRecentLogUiState) -> FoodRecentLogUiState)
    fun clear()
}