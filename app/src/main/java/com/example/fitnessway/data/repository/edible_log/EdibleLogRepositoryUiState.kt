package com.example.fitnessway.data.repository.edible_log

import com.example.fitnessway.data.model.m_26.FoodLogsCategorized
import com.example.fitnessway.util.UiState

data class EdibleLogRepositoryUiState(
    val foodLogs: Map<String, UiState<FoodLogsCategorized>> = emptyMap(),
)