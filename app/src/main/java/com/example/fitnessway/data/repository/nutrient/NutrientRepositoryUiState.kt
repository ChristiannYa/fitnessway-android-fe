package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.UiState

data class NutrientRepositoryUiState(
    val nutrientsUiState: UiState<NutrientsByType<NutrientWithPreferences>> = UiState.Loading,
    val nutrientIntakesCache: Map<String, UiState<NutrientIntakesByType>> = emptyMap(),
)