package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.nutrient.NutrientColorsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientIdWithColor
import com.example.fitnessway.data.model.nutrient.NutrientIdWithGoal
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface INutrientRepository {
    val uiState: StateFlow<NutrientRepositoryUiState>

    fun refreshNutrientIntakes(date: String)
    fun loadNutrientIntakes(date: String)

    fun refreshNutrients()
    fun loadNutrients()
    fun setNutrientGoals(request: NutrientGoalsPostRequest): Flow<UiState<List<NutrientIdWithGoal>>>
    fun setNutrientColors(request: NutrientColorsPostRequest): Flow<UiState<List<NutrientIdWithColor>>>

    fun updateState(update: (NutrientRepositoryUiState) -> NutrientRepositoryUiState)
}
