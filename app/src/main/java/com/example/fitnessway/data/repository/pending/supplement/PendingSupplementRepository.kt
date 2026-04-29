package com.example.fitnessway.data.repository.pending.supplement

import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.PendingEdibleApiClient
import com.example.fitnessway.data.repository.pending.PendingEdibleRepository
import kotlinx.coroutines.CoroutineScope

class PendingSupplementRepository(
    httpClient: HttpClient,
    apiClient: PendingEdibleApiClient,
    repositoryScope: CoroutineScope
) : IPendingSupplementRepository,
    PendingEdibleRepository<PendingSupplementRepositoryUiState>(
        edibleType = EdibleType.SUPPLEMENT,
        httpClient = httpClient,
        apiClient = apiClient,
        repositoryScope = repositoryScope,
        initialState = PendingSupplementRepositoryUiState()
    )