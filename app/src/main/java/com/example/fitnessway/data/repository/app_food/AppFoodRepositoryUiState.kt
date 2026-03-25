package com.example.fitnessway.data.repository.app_food

import com.example.fitnessway.data.model.m_26.FoodSearchResult
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager

data class AppFoodRepositoryUiState(
    val appFoodsUiStatePager: UiStatePager<FoodSearchResult> = UiStatePager(UiState.Idle),
)