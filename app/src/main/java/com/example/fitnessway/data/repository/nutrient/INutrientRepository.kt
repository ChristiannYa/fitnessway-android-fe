package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface INutrientRepository {
    suspend fun getNutrientIntakes(date: String): Flow<UiState<NutrientsByType>>
}