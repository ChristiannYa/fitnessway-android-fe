package com.example.fitnessway.data.repository.pending.food

import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.PendingEdibleApiClient
import com.example.fitnessway.data.repository.pending.PendingEdibleRepository
import kotlinx.coroutines.CoroutineScope

class PendingFoodRepository(
    httpClient: HttpClient,
    apiClient: PendingEdibleApiClient,
    repositoryScope: CoroutineScope
) : IPendingFoodRepository,
    PendingEdibleRepository<PendingFoodRepositoryUiState>(
        edibleType = EdibleType.FOOD,
        httpClient = httpClient,
        apiClient = apiClient,
        repositoryScope = repositoryScope,
        initialState = PendingFoodRepositoryUiState()
    )