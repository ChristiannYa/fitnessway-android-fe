package com.example.fitnessway.feature.lists.viewmodel

import com.example.fitnessway.util.UiState

data class ListsScreenUiState(
    val foodRequestAddState: UiState<Unit> = UiState.Idle,
    val foodAddState: UiState<Unit> = UiState.Idle,
    val foodUpdateState: UiState<Unit> = UiState.Idle,
    val foodDeleteState: UiState<Unit> = UiState.Idle,
    val foodReviewDismissState: UiState<Unit> = UiState.Idle
)