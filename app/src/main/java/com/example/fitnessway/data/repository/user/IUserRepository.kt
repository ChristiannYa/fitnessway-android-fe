package com.example.fitnessway.data.repository.user

import com.example.fitnessway.data.model.MUser.Model.User
import com.example.fitnessway.data.model.m_26.UserTimezoneSetRequest
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun getUser(): Flow<UiState<User>>
    suspend fun setTimezone(request: UserTimezoneSetRequest): Flow<UiState<Unit>>
}