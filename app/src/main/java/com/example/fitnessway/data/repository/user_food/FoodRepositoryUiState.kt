package com.example.fitnessway.data.repository.user_food

import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.m_26.FoodLogsCategorized
import com.example.fitnessway.util.UiState

data class FoodRepositoryUiState(
    val foodsUiState: UiState<List<FoodInformation>> = UiState.Loading,
    val foodLogsUiState: Map<String, UiState<FoodLogsCategorized>> = emptyMap(),
    val foodSortUiState: UiState<String> = UiState.Loading
)