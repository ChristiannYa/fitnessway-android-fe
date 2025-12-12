package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.nutrient.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientIdWithGoal
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.nutrient.INutrientApiService
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class NutrientRepositoryImpl(
    private val apiService: INutrientApiService,
    private val httpClient: HttpClient,
    private val repositoryScope: CoroutineScope
) : INutrientRepository {

    private val _uiState = MutableStateFlow(NutrientRepositoryUiState())
    override val uiState: StateFlow<NutrientRepositoryUiState> = _uiState.asStateFlow()

    override fun loadNutrients() {
        val nutrientsUiState = _uiState.value.nutrientsState

        // Skip if we have successful data
        if (nutrientsUiState is UiState.Success) {
            return
        }

        repositoryScope.launch {
            httpClient.makeRequest(
                apiCall = apiService::getNutrients,
                extractData = { it.nutrients },
                errMsg = "Failed to get nutrients"
            ).collect { state ->
                _uiState.update { it.copy(nutrientsState = state) }
            }
        }
    }

    override fun loadNutrientIntakes(date: String) {
        repositoryScope.launch {
            httpClient.makeRequest(
                apiCall = { apiService.getNutrientIntakes(date) },
                extractData = { it.nutrientIntakes },
                errMsg = "Failed to get nutrient intakes"
            ).collect { state ->
                _uiState.update { it.copy(nutrientIntakesState = state) }
            }
        }
    }

    override fun setNutrientGoals(
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

    override fun updateState(update: (NutrientRepositoryUiState) -> NutrientRepositoryUiState) {
        _uiState.update(update)
    }
}