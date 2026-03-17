package com.example.fitnessway.data.repository.auth

import com.example.fitnessway.data.model.MAuth
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    suspend fun login(
        req: MAuth.Api.Req.LoginRequest
    ): Flow<UiState<Unit>>

    // logout won't receive the refresh token because `AuthRepositoryImpl` already
    // has access to the tokens, so making the viewmodel also receive the tokens would
    // be repetitive
    suspend fun logout(): Flow<UiState<Unit>>

    suspend fun register(
        req: MAuth.Api.Req.RegisterRequest
    ): Flow<UiState<Unit>>
}