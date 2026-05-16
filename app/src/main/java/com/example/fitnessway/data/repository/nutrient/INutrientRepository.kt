package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.MNutrient.Api.Req.NutrientColorsPostRequest
import com.example.fitnessway.data.model.MNutrient.Api.Req.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithColor
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithGoal
import com.example.fitnessway.data.repository.IRepository
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface INutrientRepository : IRepository<NutrientRepositoryUiState> {
    fun load()

    fun setGoals(request: NutrientGoalsPostRequest): Flow<UiState<List<NutrientIdWithGoal>>>
    fun setColors(request: NutrientColorsPostRequest): Flow<UiState<List<NutrientIdWithColor>>>
}
