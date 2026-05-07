package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.MNutrient.Model.NutrientWithPreferences
import com.example.fitnessway.data.model.MNutrient.Model.NutrientsByType
import com.example.fitnessway.util.UiState

data class NutrientRepositoryUiState(
    val nutrients: UiState<NutrientsByType<NutrientWithPreferences>> = UiState.Loading,
)