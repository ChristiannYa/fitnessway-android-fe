package com.example.fitnessway.data.repository.edible_list.food

import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.UserEdibleApiClient
import com.example.fitnessway.data.repository.edible_list.UserEdibleRepository
import kotlinx.coroutines.CoroutineScope

class UserFoodRepositoryImpl(
    httpClient: HttpClient,
    apiClient: UserEdibleApiClient,
    repositoryScope: CoroutineScope
) : IUserFoodRepository,
    UserEdibleRepository<UserFoodRepositoryUiState>(
        edibleType = EdibleType.FOOD,
        httpClient = httpClient,
        apiClient = apiClient,
        repositoryScope = repositoryScope,
        initialState = UserFoodRepositoryUiState()
    )