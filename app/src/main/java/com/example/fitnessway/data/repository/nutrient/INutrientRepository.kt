package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.nutrient.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import kotlinx.coroutines.flow.StateFlow

interface INutrientRepository {
    val uiState: StateFlow<NutrientRepositoryUiState>

    fun loadNutrients()
    fun loadNutrientIntakes(date: String)

    fun setNutrientGoals(
        request: NutrientGoalsPostRequest,
        originalData: NutrientsByType<NutrientWithPreferences>
    )

    fun updateState(update: (NutrientRepositoryUiState) -> NutrientRepositoryUiState)
}
