package com.example.fitnessway.feature.home.viewmodel

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.util.UiState

data class HomeScreenUiState(
    val foodAddState: UiState<FoodInformation> = UiState.Idle,
    val foodLogAddState: UiState<FoodLogData> = UiState.Idle,
    val foodLogUpdateState: UiState<FoodLogData> = UiState.Idle,
    val foodLogDeleteState: UiState<FoodLogData> = UiState.Idle
)