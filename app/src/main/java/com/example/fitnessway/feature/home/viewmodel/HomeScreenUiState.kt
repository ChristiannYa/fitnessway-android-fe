package com.example.fitnessway.feature.home.viewmodel

import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.util.UiState

data class HomeScreenUiState(
    val foodRequestState: UiState<PendingFood> = UiState.Idle,
    val foodLogAddState: UiState<FoodLogData> = UiState.Idle,
    val foodLogUpdateState: UiState<FoodLogData> = UiState.Idle,
    val foodLogDeleteState: UiState<FoodLogData> = UiState.Idle,
    val foodSortUpdateState: UiState<String> = UiState.Idle // @TODO: Remove state
)