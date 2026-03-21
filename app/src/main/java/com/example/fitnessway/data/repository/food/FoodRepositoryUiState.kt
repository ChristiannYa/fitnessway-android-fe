package com.example.fitnessway.data.repository.food

import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.MFood.Model.FoodLogsByCategory
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager

data class FoodRepositoryUiState(
    val pendingFoodsUiStatePager: UiStatePager<PendingFood> = UiStatePager(),
    val foodsUiState: UiState<List<FoodInformation>> = UiState.Loading,
    val foodLogsUiState: Map<String, UiState<FoodLogsByCategory>> = emptyMap(),
    val foodSortUiState: UiState<String> = UiState.Loading
)