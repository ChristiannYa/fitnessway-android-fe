package com.example.fitnessway.feature.profile.viewmodel

import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithColor
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithGoal
import com.example.fitnessway.util.UiState

data class ProfileUiState(
    val nutrientGoalsSetUiState: UiState<List<NutrientIdWithGoal>> = UiState.Idle,
    val nutrientColorsSetUiState: UiState<List<NutrientIdWithColor>> = UiState.Idle,
    val logoutState: UiState<Unit> = UiState.Idle
)