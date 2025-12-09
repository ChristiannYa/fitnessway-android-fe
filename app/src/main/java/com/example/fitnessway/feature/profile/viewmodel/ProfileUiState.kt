package com.example.fitnessway.feature.profile.viewmodel

import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientIdWithGoal
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.UiState

data class ProfileUiState(
    val nutrientsState: UiState<NutrientsByType<NutrientApiFormat>> = UiState.Loading,
    val nutrientGoalsPostState: UiState<List<NutrientIdWithGoal>> = UiState.Idle,
    val logoutState: UiState<Unit> = UiState.Idle
)