package com.example.fitnessway.data.repository.nutrient_intakes

import com.example.fitnessway.data.model.m_26.NutrientIntakes
import com.example.fitnessway.util.UiState

data class NutrientIntakesRepositoryUiState(
    val nutrientIntakes: Map<String, UiState<NutrientIntakes>> = emptyMap()
)