package com.example.fitnessway.data.repository.user_food

import com.example.fitnessway.data.model.m_26.UserEdible
import com.example.fitnessway.util.UiStatePager

data class FoodRepositoryUiState(
    val foodsUiStatePager: UiStatePager<UserEdible> = UiStatePager()
)