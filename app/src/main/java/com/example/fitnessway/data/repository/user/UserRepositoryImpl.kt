package com.example.fitnessway.data.repository.user

import com.example.fitnessway.data.model.MUser.Model.User
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.UserApiClient
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: UserApiClient
) : IUserRepository {

    override suspend fun getUser(): Flow<UiState<User>> =
        httpClient.makeRequest(
            apiCall = apiClient::getUser,
            extractData = { it.user },
            errMsg = "Failed to get user data",
            pathDescription = "user"
        )
}
