package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientIdWithGoal
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.nutrient.INutrientApiService
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

class NutrientRepositoryImpl(
    private val apiService: INutrientApiService,
    private val httpClient: HttpClient
) : INutrientRepository {

    override suspend fun getNutrientIntakes(date: String): Flow<UiState<NutrientIntakesByType>> {
        return httpClient.makeRequest(
            apiCall = { apiService.getNutrientIntakes(date) },
            extractData = { it.nutrientIntakes },
            errMsg = "Failed to get nutrient intakes"
        )
    }

    override suspend fun getNutrients(): Flow<UiState<NutrientsByType<NutrientApiFormat>>> {
        return httpClient.makeRequest(
            apiCall = apiService::getNutrients,
            extractData = { it.nutrients },
            errMsg = "Failed to get nutrients"
        )
    }

    override suspend fun setNutrientGoals(
        request: NutrientGoalsPostRequest
    ): Flow<UiState<List<NutrientIdWithGoal>>> {
        return httpClient.makeRequest(
            apiCall = { apiService.setNutrientGoals(request) },
            extractData = { it.upsertedGoals },
            errMsg = "Failed to set nutrient goals",
            invalidatedUrls = listOf(
                ApiUrls.Nutrient.ALL_INTAKES,
                ApiUrls.Nutrient.NUTRIENTS,
                ApiUrls.Food.ALL_LOGS,
                ApiUrls.Food.FOODS
            )
        )
    }
}