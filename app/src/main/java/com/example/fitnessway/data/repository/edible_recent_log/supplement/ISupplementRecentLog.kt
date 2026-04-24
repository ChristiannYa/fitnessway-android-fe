package com.example.fitnessway.data.repository.edible_recent_log.supplement

import kotlinx.coroutines.flow.StateFlow

interface ISupplementRecentLog {
    val uiState: StateFlow<SupplementRecentLogUiState>

    fun refresh()
    fun load()
    fun loadMore()

    fun updateState(update: (SupplementRecentLogUiState) -> SupplementRecentLogUiState)
    fun clear()
}