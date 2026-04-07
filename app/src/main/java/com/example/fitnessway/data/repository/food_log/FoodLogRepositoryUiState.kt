package com.example.fitnessway.data.repository.food_log

import com.example.fitnessway.data.model.m_26.FoodLogsCategorized
import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager

data class FoodLogRepositoryUiState(
    val foodLogs: Map<String, UiState<FoodLogsCategorized>> = emptyMap(),
    val recentlyLogged: UiStatePager<FoodPreview> = UiStatePager(UiState.Loading)
)