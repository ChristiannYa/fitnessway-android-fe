package com.example.fitnessway.data.repository.edible_list.supplement

import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.UserEdibleApiClient
import com.example.fitnessway.data.repository.edible_list.UserEdibleRepository
import kotlinx.coroutines.CoroutineScope

class UserSupplementRepositoryImpl(
    httpClient: HttpClient,
    apiClient: UserEdibleApiClient,
    repositoryScope: CoroutineScope
) : IUserSupplementRepository,
    UserEdibleRepository<UserSupplementUiState>(
        edibleType = EdibleType.SUPPLEMENT,
        httpClient = httpClient,
        apiClient = apiClient,
        repositoryScope = repositoryScope
    ) {
    override val initialState = UserSupplementUiState()
}