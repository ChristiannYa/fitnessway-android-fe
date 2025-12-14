package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.nutrient.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientIdWithGoal
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.model.nutrient.NutrientsByType
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

    private fun fetchNutrientIntakes(date: String): Flow<UiState<NutrientIntakesByType>> {
        return httpClient.makeRequest(
            apiCall = { apiService.getNutrientIntakes(date) },
            extractData = { it.nutrientIntakes },
            errMsg = "Failed to get nutrient intakes"
        )
    }

    override fun refreshNutrientIntakes(date: String) {
        repositoryScope.launch {
            fetchNutrientIntakes(date).collect { state ->
                _uiState.update { it.copy(nutrientIntakesCache = it.nutrientIntakesCache + (date to state)) }
            }
        }
    }

    override fun loadNutrientIntakes(date: String) {
        val cachedData = _uiState.value.nutrientIntakesCache[date]
        if (cachedData is UiState.Success) {
            return
        }
        refreshNutrientIntakes(date)
    }

    private fun fetchNutrients(): Flow<UiState<NutrientsByType<NutrientWithPreferences>>> {
        return httpClient.makeRequest(
            apiCall = apiService::getNutrients,
            extractData = { it.nutrients },
            errMsg = "Failed to fetch nutrients"
        )
    }

    override fun refreshNutrients() {
        repositoryScope.launch {
            fetchNutrients().collect { state ->
                _uiState.update { it.copy(nutrientsUiState = state) }
            }
        }
    }

    override fun loadNutrients() {
        val nutrientsUiState = _uiState.value.nutrientsUiState
        if (nutrientsUiState is UiState.Success) return
        refreshNutrients()
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