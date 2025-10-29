package com.example.fitnessway.data.repository.user

import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun getUser(): Flow<UiState<User>>
}