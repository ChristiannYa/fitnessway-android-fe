package com.example.fitnessway.feature.home.viewmodel

import com.example.fitnessway.data.model.food.FoodLogsByCategory
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.UiState

data class HomeScreenUiState(
    val nutrientIntakesState: UiState<NutrientIntakesByType> = UiState.Loading,
    val foodLogsState: UiState<FoodLogsByCategory> = UiState.Loading
)