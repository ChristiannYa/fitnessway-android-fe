package com.example.fitnessway.data.repository.auth

import com.example.fitnessway.data.model.MAuth
import com.example.fitnessway.data.model.MAuth.Api.Req.LogoutRequest
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.AuthApiClient
import com.example.fitnessway.data.state.token.ITokensStateHolder
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: AuthApiClient,
    private val tokensStateHolder: ITokensStateHolder,
    private val userStateHolder: IUserStateHolder,
) : IAuthRepository {
    override suspend fun register(req: MAuth.Api.Req.RegisterRequest): Flow<UiState<Unit>> =
        httpClient.makeRequest(
            apiCall = { apiClient.register(req) },
            extractData = {
                tokensStateHolder.setAccessToken(it.accessToken)
                tokensStateHolder.setRefreshToken(it.refreshToken)
            }
        )

    override suspend fun login(req: MAuth.Api.Req.LoginRequest): Flow<UiState<Unit>> =
        httpClient.makeRequest(
            apiCall = { apiClient.login(req) },
            extractData = {
                tokensStateHolder.setAccessToken(it.accessToken)
                tokensStateHolder.setRefreshToken(it.refreshToken)
            }
        )

    override suspend fun logout(): Flow<UiState<Unit>> = flow {
        emit(UiState.Loading)

        val refreshToken = tokensStateHolder.tokensState.value.refreshToken

        if (refreshToken == null) {
            clearCachedData()
            emit(UiState.Success(Unit))
            return@flow
        }

        try {
            apiClient.logout(LogoutRequest(refreshToken))
        } catch (e: Exception) {
            logcat("logout exception: ${e.message}")
        } finally {
            // Always clear auth regardless of success or failure
            clearCachedData()
            emit(UiState.Success(Unit))
        }
    }.flowOn(Dispatchers.IO)

    private fun clearCachedData() {
        tokensStateHolder.clearTokens()
        userStateHolder.clearUser()
    }
}