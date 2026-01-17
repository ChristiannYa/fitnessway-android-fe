package com.example.fitnessway.data.repository.auth

import com.example.fitnessway.data.model.MAuth.Api.Req.LoginRequest
import com.example.fitnessway.data.model.MAuth.Api.Req.RegisterRequest
import com.example.fitnessway.data.model.MAuth.Api.Req.LogoutRequest
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
    private val userStateHolder: IUserStateHolder
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
                response.code() == 409 -> {
                    emit(UiState.Error("This email is already in use"))
                }

                response.isSuccessful -> {
                    val body = response.body()

                    if (body != null && body.data != null) {
                        tokensStateHolder.setTokens(
                            refreshToken = body.data.refreshToken,
                            accessToken = body.data.accessToken
                        )
                        emit(UiState.Success(Unit))
                    } else {
                        emit(UiState.Error("Registration failed"))
                    }
                }

                else -> {
                    val errMsg = response.errorBody()?.string()
                    logcat(errMsg.toString())
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
                        // Success is only true when the api returns both tokens, so we are
                        // use assert operators because we are sure that both tokens are present
                        refreshToken = body.data.refreshToken,
                        accessToken = body.data.accessToken
                    )
                    emit(
                        UiState.Success(data = Unit)
                    )
                } else {
                    // Handle edge cases when there is a dependency error e.g., Retrofit bug,
                    // Serialization fail
                    emit(UiState.Error("Login failed"))
                }
            } else { // HTTP 400/401/500 error codes
                val errMsg = response.message()
                if (errMsg == "invalid email or password") {
                    emit(UiState.Error(errMsg))
                } else {
                    emit(UiState.Error("Login failed"))
                }
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
            tokensStateHolder.clearTokens()
            userStateHolder.clearUser()
            emit(UiState.Success(Unit))
            // complete `flow {` lambda above
            // The code below this block won't be called
            return@flow
        }

        try {
            val response = authApiAuthorizedService.logout(
                LogoutRequest(refreshToken)
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true) {
                    hardLogout()
                    emit(UiState.Success(Unit))
                } else {
                    // Still clear auth even if API returns an error
                    hardLogout()
                    emit(UiState.Error("Logout failed"))
                }
            } else {
                // Still clear auth even on HTTP error
                logcat(response.message())
                hardLogout()
                emit(UiState.Error("Logout failed"))
            }
        } catch (e: Exception) {
            logcat("logout exception: ${e.message}")
            // Still clear auth even on network error
            hardLogout()
            emit(UiState.Error("Logout network error"))
        }
    }.flowOn(Dispatchers.IO)

    private val hardLogout = {
        tokensStateHolder.clearTokens()
        userStateHolder.clearUser()
    }
}