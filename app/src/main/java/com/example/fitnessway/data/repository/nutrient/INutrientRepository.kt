package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.nutrient.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientIdWithGoal
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface INutrientRepository {
    suspend fun getNutrientIntakes(date: String): Flow<UiState<NutrientIntakesByType>>
    suspend fun getNutrients(): Flow<UiState<NutrientsByType<NutrientWithPreferences>>>
    suspend fun setNutrientGoals(request: NutrientGoalsPostRequest): Flow<UiState<List<NutrientIdWithGoal>>>
}