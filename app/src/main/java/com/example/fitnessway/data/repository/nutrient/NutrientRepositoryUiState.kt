package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.UiState

data class NutrientRepositoryUiState(
    val nutrientsState: UiState<NutrientsByType<NutrientWithPreferences>> = UiState.Loading,
    val nutrientIntakesState: UiState<NutrientIntakesByType> = UiState.Loading,
)