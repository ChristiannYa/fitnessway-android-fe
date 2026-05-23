package com.example.fitnessway.data.repository.app_food

import com.example.fitnessway.data.model.m_26.AppEdible
import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager

data class AppEdibleRepositoryUiState(
    val appEdiblesUiStatePager: UiStatePager<FoodPreview> = UiStatePager(UiState.Idle),
    val appEdible: UiState<AppEdible?> = UiState.Idle
)