package com.example.fitnessway.feature.lists.viewmodel

import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.util.UiState

data class ListsScreenUiState(
    val foodRequestState: UiState<PendingFood> = UiState.Idle,
    val foodAddState: UiState<Unit> = UiState.Idle,
    val foodUpdateState: UiState<Unit> = UiState.Idle,
    val edibleDeleteState: UiState<Unit> = UiState.Idle,
    val reviewDismissState: UiState<Unit> = UiState.Idle
)