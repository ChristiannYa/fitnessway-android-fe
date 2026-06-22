package com.example.fitnessway.feature.home.viewmodel

import com.example.fitnessway.util.UiState

data class HomeScreenUiState(
    val foodLogAddState: UiState<Unit> = UiState.Idle,
    val foodLogUpdateState: UiState<Unit> = UiState.Idle,
    val foodLogDeleteState: UiState<Unit> = UiState.Idle,
)