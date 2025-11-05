package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.network.Http
import com.example.fitnessway.data.network.nutrient.INutrientApiService
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

class NutrientRepositoryImpl(
    private val apiService: INutrientApiService
) : INutrientRepository {

    override suspend fun getNutrientIntakes(date: String): Flow<UiState<NutrientIntakesByType>> {
        return Http.makeRequest(
            apiCall = { apiService.getNutrientIntakes(date) },
            extractData = { it.nutrientIntakes },
            errMsg = "Failed to get nutrient intakes"
        )
    }
}