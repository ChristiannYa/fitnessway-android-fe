package com.example.fitnessway.data.repository.user

import com.example.fitnessway.data.model.MUser.Model.User
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.RetrofitService.IUser
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val apiService: IUser,
    private val httpClient: HttpClient
) : IUserRepository {

    override suspend fun getUser(): Flow<UiState<User>> {
        return httpClient.makeRequest(
            apiCall = apiService::getUser,
            extractData = { it.user },
            errMsg = "Failed to get user data"
        )
    }
}
