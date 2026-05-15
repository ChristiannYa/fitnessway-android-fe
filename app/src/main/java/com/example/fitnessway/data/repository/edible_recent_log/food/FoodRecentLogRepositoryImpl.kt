package com.example.fitnessway.data.repository.edible_recent_log.food

import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.EdibleRecentLogApiClient
import com.example.fitnessway.data.repository.edible_recent_log.EdibleRecentLogRepository
import kotlinx.coroutines.CoroutineScope

class FoodRecentLogRepositoryImpl(
    httpClient: HttpClient,
    apiClient: EdibleRecentLogApiClient,
    repositoryScope: CoroutineScope
) : IFoodRecentLogRepository,
    EdibleRecentLogRepository<FoodRecentLogRepositoryUiState>(
        edibleType = EdibleType.FOOD,
        httpClient = httpClient,
        apiClient = apiClient,
        repositoryScope = repositoryScope,
        initialState = FoodRecentLogRepositoryUiState()
    )