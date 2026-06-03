package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.m_26.NutrientData
import com.example.fitnessway.data.model.m_26.NutrientsByType
import com.example.fitnessway.util.UiState

data class NutrientRepositoryUiState(
    val nutrients: UiState<NutrientsByType<NutrientData>> = UiState.Loading,
)