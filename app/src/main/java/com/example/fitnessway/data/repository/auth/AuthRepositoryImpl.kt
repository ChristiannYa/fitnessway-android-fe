package com.example.fitnessway.data.repository.auth

import com.example.fitnessway.data.model.MAuth.Api.Req.LoginRequest
import com.example.fitnessway.data.model.MAuth.Api.Req.LogoutRequest
import com.example.fitnessway.data.model.MAuth.Api.Req.RegisterRequest
import com.example.fitnessway.data.network.CacheManager
import com.example.fitnessway.data.network.RetrofitService.IAuthorized
import com.example.fitnessway.data.network.RetrofitService.IUnauthorized
import com.example.fitnessway.data.state.token.ITokensStateHolder
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AuthRepositoryImpl(
    private val authApiService: IUnauthorized,
    private val authApiAuthorizedService: IAuthorized,
    private val tokensStateHolder: ITokensStateHolder,
    private val userStateHolder: IUserStateHolder,
    private val cacheManager: CacheManager
) : IAuthRepository {
    override suspend fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        deviceName: String
    ): Flow<UiState<Unit>> = flow {
        emit(UiState.Loading)

        try {
            val response = authApiService.register(
                RegisterRequest(
                    name,
                    email,
                    password,
                    confirmPassword,
                    deviceName
                )
            )

            when {
                response.code() == 409 -> emit(UiState.Error("This email is already in use"))

                response.isSuccessful -> {
                    val body = response.body()

                    if (body != null && body.data != null) {
                        tokensStateHolder.setTokens(
                            refreshToken = body.data.refreshToken,
                            accessToken = body.data.accessToken
                        )
                        emit(UiState.Success(Unit))
                    } else emit(UiState.Error("Registration failed"))
                }

                else -> {
                    val errMsg = response.errorBody()?.string()
                    logcat("registration failed: $errMsg")
                    emit(UiState.Error("Registration failed"))
                }
            }
        } catch (e: Exception) {
            logcat("register exception: ${e.message}")
            emit(UiState.Error("Registration network error"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun login(
        email: String,
        password: String,
        deviceName: String
    ): Flow<UiState<Unit>> = flow {
        emit(UiState.Loading)

        try {
            val response = authApiService.login(
                request = LoginRequest(email, password, deviceName)
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null && body.data != null) {
                    tokensStateHolder.setTokens(
                        refreshToken = body.data.refreshToken,
                        accessToken = body.data.accessToken
                    )
                    emit(UiState.Success(data = Unit))
                } else {
                    // Handle edge cases when there is a dependency error e.g., Retrofit bug,
                    // Serialization fail
                    emit(UiState.Error("Login failed"))
                }
            } else { // HTTP 400/401/500 error codes
                val responseCode = response.code()

                if (responseCode != 400 && responseCode != 401) {
                    logcat("login error: ${response.message()}")
                }

                val errorMessage = when (responseCode) {
                    400 -> "Bad request"
                    401 -> "Invalid email or password"
                    500 -> "Server error"
                    else -> "Login failed"
                }

                emit(UiState.Error(errorMessage))
            }
        } catch (e: Exception) {
            // Network errors
            logcat("login exception: ${e.message}")
            emit(UiState.Error("Login network error"))
        }

    }.flowOn(Dispatchers.IO)

    override suspend fun logout(): Flow<UiState<Unit>> = flow {
        emit(UiState.Loading)

        val refreshToken = tokensStateHolder.tokensState.value.refreshToken

        if (refreshToken == null) {
            emit(UiState.Success(Unit))
            clearCachedData()
            return@flow
        }

        try {
            val response = authApiAuthorizedService.logout(
                LogoutRequest(refreshToken)
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true) {
                    emit(UiState.Success(Unit))
                    clearCachedData()
                } else {
                    // Still clear auth even if API returns an error
                    clearCachedData()
                }
            } else {
                // Still clear auth even on HTTP error
                logcat(response.message())
                clearCachedData()
            }
        } catch (e: Exception) {
            logcat("logout exception: ${e.message}")
            // Still clear auth even on network error
            clearCachedData()
        }
    }.flowOn(Dispatchers.IO)

    private val clearCachedData = {
        tokensStateHolder.clearTokens()
        userStateHolder.clearUser()
        cacheManager.evictAll()
    }
}