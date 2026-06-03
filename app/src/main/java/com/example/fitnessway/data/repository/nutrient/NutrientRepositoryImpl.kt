package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.MNutrient.Api.Req.NutrientColorsPostRequest
import com.example.fitnessway.data.model.MNutrient.Api.Req.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithColor
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithGoal
import com.example.fitnessway.data.model.m_26.NutrientData
import com.example.fitnessway.data.model.m_26.NutrientsByType
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

    private fun fetch(): Flow<UiState<NutrientsByType<NutrientData>>> =
        httpClient.makeRequest(
            apiCall = apiClient::get,
            extractData = { it.nutrientsByType },
            errMsg = "Failed to get nutrients",
            pathDescription = "nutrient list"
        )

    override fun refresh() {
        _uiState.update { it.copy(nutrients = UiState.Loading) }

        repositoryScope.launch {
            fetch().collect { state ->
                _uiState.update { it.copy(nutrients = state) }
            }
        }
    }

    override fun load() {
        val nutrientsUiState = _uiState.value.nutrients
        if (nutrientsUiState.hasResult) return
        refresh()
    }

    override fun setGoals(
        request: NutrientGoalsPostRequest
    ): Flow<UiState<List<NutrientIdWithGoal>>> =
        httpClient.makeRequest(
            apiCall = { apiClient.setGoals(request) },
            extractData = { it.upsertedGoals },
            errMsg = "Failed to set nutrient goals",
            pathDescription = "set nutrient goal(s)"
        )

    override fun setColors(
        request: NutrientColorsPostRequest
    ): Flow<UiState<List<NutrientIdWithColor>>> =
        httpClient.makeRequest(
            apiCall = { apiClient.setColors(request) },
            extractData = { it.updatedColors },
            errMsg = "Failed to update nutrient colors",
            pathDescription = "ser nutrient color(s)"
        )

    override fun update(update: (NutrientRepositoryUiState) -> NutrientRepositoryUiState) =
        _uiState.update(update)

    override fun clear() =
        _uiState.update { NutrientRepositoryUiState() }
}