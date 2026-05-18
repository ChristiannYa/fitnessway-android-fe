package com.example.fitnessway.data.repository.nutrient_intakes

import com.example.fitnessway.data.model.m_26.NutrientIntakes
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.NutrientIntakeApiClient
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.logcat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NutrientIntakesRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: NutrientIntakeApiClient,
    private val repositoryScope: CoroutineScope
) : INutrientIntakesRepository {

    private val _uiState = MutableStateFlow(NutrientIntakesRepositoryUiState())
    override val uiState: StateFlow<NutrientIntakesRepositoryUiState> = _uiState

    private val _date = MutableStateFlow("")

    override fun setDate(date: String) {
        _date.value = date
    }

    private fun fetch(date: String): Flow<UiState<NutrientIntakes>> =
        httpClient.makeRequest(
            apiCall = { apiClient.get(date) },
            extractData = { it.nutrientIntakes },
            errMsg = "Failed to get nutrient intakes",
            pathDescription = "nutrient intakes"
        )

    override fun refresh() {
        val dateValue = _date.value

        repositoryScope.launch {
            fetch(dateValue).collect { state ->
                _uiState.update { it.copy(nutrientIntakes = it.nutrientIntakes + (dateValue to state)) }
            }
        }
    }

    override fun load() {
        val uiState = _uiState.value.nutrientIntakes[_date.value]
        uiState?.let { if (it.hasResult) return }
        refresh()
    }

    override fun update(update: (NutrientIntakesRepositoryUiState) -> NutrientIntakesRepositoryUiState) =
        _uiState.update(update)

    override fun clear() {
        logcat("clearing nutrient intakes repo")
        _uiState.update { NutrientIntakesRepositoryUiState() }
    }
}