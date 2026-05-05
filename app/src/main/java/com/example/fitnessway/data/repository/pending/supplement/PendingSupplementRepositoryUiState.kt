package com.example.fitnessway.data.repository.pending.supplement

import com.example.fitnessway.data.model.m_26.PendingEdible
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.util.UiStatePager

data class PendingSupplementRepositoryUiState(
    override val uiStatePager: UiStatePager<PendingEdible> = UiStatePager()
) : RepositoryPagerState<PendingEdible, PendingSupplementRepositoryUiState> {

    override fun copyWithPager(pager: UiStatePager<PendingEdible>): PendingSupplementRepositoryUiState =
        copy(uiStatePager = pager)
}