package com.example.fitnessway.data.repository.user_supplement

import kotlinx.coroutines.flow.StateFlow

interface IUserSupplementRepository {
    val uiState: StateFlow<UserSupplementRepositoryUiState>

    fun refresh()
    fun load()
    fun loadMore()

    fun updateState(update: (UserSupplementRepositoryUiState) -> UserSupplementRepositoryUiState)
    fun clear()
}