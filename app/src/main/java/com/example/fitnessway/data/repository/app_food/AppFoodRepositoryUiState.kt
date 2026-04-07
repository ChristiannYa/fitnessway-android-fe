package com.example.fitnessway.data.repository.app_food

import com.example.fitnessway.data.model.m_26.AppFood
import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager

data class AppFoodRepositoryUiState(
    val appFoodsUiStatePager: UiStatePager<FoodPreview> = UiStatePager(UiState.Idle),
    val appFood: UiState<AppFood?> = UiState.Idle
)