package com.example.fitnessway.feature.lists.viewmodel

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.util.UiState

data class ListsScreenUiState(
    val foodsState: UiState<List<FoodInformation>> = UiState.Loading,
    val foodUpdateState: UiState<FoodInformation> = UiState.Idle,
    val foodDeleteState: UiState<FoodInformation> = UiState.Idle
)