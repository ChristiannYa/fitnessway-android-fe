package com.example.fitnessway.data.repository.food

import com.example.fitnessway.data.model.MFood.Model.FoodLogsByCategory
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.util.UiState

data class FoodRepositoryUiState(
    val foodsUiState: UiState<List<FoodInformation>> = UiState.Loading,
    val foodLogsCache: Map<String, UiState<FoodLogsByCategory>> = emptyMap(),
    val foodSortUiState: UiState<String> = UiState.Loading
)