package com.example.fitnessway.feature.profile.viewmodel

import com.example.fitnessway.util.UiState

data class ProfileUiState(
    val logoutState: UiState<Unit> = UiState.Idle
)