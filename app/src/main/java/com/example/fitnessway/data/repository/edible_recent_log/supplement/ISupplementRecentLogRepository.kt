package com.example.fitnessway.data.repository.edible_recent_log.supplement

import kotlinx.coroutines.flow.StateFlow

interface ISupplementRecentLogRepository {
    val uiState: StateFlow<SupplementRecentLogRepositoryUiState>

    fun refresh()
    fun load()
    fun loadMore()

    fun updateState(update: (SupplementRecentLogRepositoryUiState) -> SupplementRecentLogRepositoryUiState)
    fun clear()
}