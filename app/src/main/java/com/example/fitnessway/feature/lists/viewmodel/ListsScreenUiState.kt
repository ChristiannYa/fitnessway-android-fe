package com.example.fitnessway.feature.lists.viewmodel

import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.util.UiState

data class ListsScreenUiState(
    val foodAddState: UiState<FoodInformation> = UiState.Idle,
    val foodUpdateState: UiState<FoodInformation> = UiState.Idle,
    val foodDeleteState: UiState<FoodInformation> = UiState.Idle
)