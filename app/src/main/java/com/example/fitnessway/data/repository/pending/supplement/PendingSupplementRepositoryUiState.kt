package com.example.fitnessway.data.repository.pending.supplement

import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.util.UiStatePager

data class PendingSupplementRepositoryUiState(
    override val uiStatePager: UiStatePager<PendingFood> = UiStatePager()
) : RepositoryPagerState<PendingFood, PendingSupplementRepositoryUiState> {

    override fun copyWithPager(pager: UiStatePager<PendingFood>): PendingSupplementRepositoryUiState =
        copy(uiStatePager = pager)
}