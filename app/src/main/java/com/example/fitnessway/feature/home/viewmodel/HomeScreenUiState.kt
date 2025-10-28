package com.example.fitnessway.feature.home.viewmodel

import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.UiState

data class HomeScreenUiState(
    val nutrientIntakesState: UiState<NutrientsByType> = UiState.Loading
    // val foodLogsState: UiState<...>...
)