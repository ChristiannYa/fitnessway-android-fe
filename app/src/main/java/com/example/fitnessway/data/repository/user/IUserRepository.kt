package com.example.fitnessway.data.repository.user

import com.example.fitnessway.data.model.m_26.UserTimezoneSetRequest
import com.example.fitnessway.data.repository.IRepository
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface IUserRepository : IRepository<UserRepositoryUiState> {
    fun refresh()
    fun load()

    suspend fun setTimezone(request: UserTimezoneSetRequest): Flow<UiState<Unit>>
}