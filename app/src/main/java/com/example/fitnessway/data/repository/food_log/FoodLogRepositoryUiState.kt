package com.example.fitnessway.data.repository.food_log

import com.example.fitnessway.data.model.m_26.FoodLogsCategorized
import com.example.fitnessway.util.UiState

data class FoodLogRepositoryUiState(
    val foodLogsUiState: Map<String, UiState<FoodLogsCategorized>> = emptyMap(),
    val foodLogs: Map<String, UiState<FoodLogsCategorized>> = emptyMap(),
)