package com.example.fitnessway.data.repository.pending_food

import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.util.UiStatePager

data class PendingFoodRepositoryUiState(
    val pendingFoodsUiStatePager: UiStatePager<PendingFood> = UiStatePager()
)