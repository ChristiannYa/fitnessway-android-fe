package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.MNutrient.Api.Req.NutrientColorsPostRequest
import com.example.fitnessway.data.model.MNutrient.Api.Req.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithColor
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithGoal
import com.example.fitnessway.data.model.MNutrient.Model.NutrientDataWithAmount
import com.example.fitnessway.data.model.MNutrient.Model.NutrientWithPreferences
import com.example.fitnessway.data.model.MNutrient.Model.NutrientsByType
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.NutrientApiClient
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class NutrientRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: NutrientApiClient,
    private val repositoryScope: CoroutineScope,
) : INutrientRepository {

    private val _uiState = MutableStateFlow(NutrientRepositoryUiState())
    override val uiState: StateFlow<NutrientRepositoryUiState> = _uiState.asStateFlow()

    private fun fetchNutrientIntakes(date: String): Flow<UiState<NutrientsByType<NutrientDataWithAmount>>> {
        return httpClient.makeRequest(
            apiCall = { apiClient.getNutrientIntakes(date) },
            extractData = { it.nutrientIntakes },
            errMsg = "Failed to get nutrient intakes"
        )
    }

    override fun clearNutrientIntakesUiCache() {
        _uiState.update { it.copy(nutrientIntakesCache = emptyMap()) }
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
        if (cachedData is UiState.Success || cachedData is UiState.Error) return
        refreshNutrientIntakes(date)
    }

    private fun fetchNutrients(): Flow<UiState<NutrientsByType<NutrientWithPreferences>>> {
        return httpClient.makeRequest(
            apiCall = apiClient::getNutrients,
            extractData = { it.nutrients },
            errMsg = "Failed to get nutrients"
        )
    }

    override fun refreshNutrients() {
        _uiState.update { it.copy(nutrientsUiState = UiState.Loading) }

        repositoryScope.launch {
            fetchNutrients().collect { state ->
                _uiState.update { it.copy(nutrientsUiState = state) }
            }
        }
    }

    override fun loadNutrients() {
        val nutrientsUiState = _uiState.value.nutrientsUiState
        if (nutrientsUiState is UiState.Success || nutrientsUiState is UiState.Error) return
        refreshNutrients()
    }

    override fun setNutrientGoals(
        request: NutrientGoalsPostRequest
    ): Flow<UiState<List<NutrientIdWithGoal>>> {
        return httpClient.makeRequest(
            apiCall = { apiClient.setNutrientGoals(request) },
            extractData = { it.upsertedGoals },
            errMsg = "Failed to set nutrient goals",
            invalidatedUrls = listOf(
                ApiUrls.Nutrient.NUTRIENT_INTAKES_URL,
                ApiUrls.Nutrient.NUTRIENT_LIST_URL,
                ApiUrls.FoodLog.LIST_URL,
                ApiUrls.Food.LIST_URL
            )
        )
    }

    // @TODO: Re-fetch all nutrient related data when updating the colors
    override fun setNutrientColors(
        request: NutrientColorsPostRequest
    ): Flow<UiState<List<NutrientIdWithColor>>> {
        return httpClient.makeRequest(
            apiCall = { apiClient.setNutrientColors(request) },
            extractData = { it.updatedColors },
            errMsg = "Failed to update nutrient colors",
            invalidatedUrls = listOf(
                ApiUrls.Nutrient.NUTRIENT_INTAKES_URL,
                ApiUrls.Nutrient.NUTRIENT_LIST_URL,
                ApiUrls.FoodLog.LIST_URL,
                ApiUrls.Food.LIST_URL
            )
        )
    }

    override fun updateState(update: (NutrientRepositoryUiState) -> NutrientRepositoryUiState) {
        _uiState.update(update)
    }

    override fun clearRepository() {
        _uiState.update { NutrientRepositoryUiState() }
    }
}