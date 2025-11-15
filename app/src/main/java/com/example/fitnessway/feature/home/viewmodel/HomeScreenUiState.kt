package com.example.fitnessway.feature.home.viewmodel

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.food.FoodLogsByCategory
import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.UiState

data class HomeScreenUiState(
    val nutrientsState: UiState<NutrientsByType<NutrientApiFormat>> = UiState.Loading,
    val nutrientIntakesState: UiState<NutrientIntakesByType> = UiState.Loading,
    val foodsState: UiState<List<FoodInformation>> = UiState.Loading,
    val foodLogsState: UiState<FoodLogsByCategory> = UiState.Loading,
    val foodLogAddState: UiState<FoodLogData> = UiState.Idle,
    val foodLogDeleteState: UiState<FoodLogData> = UiState.Idle
)