package com.example.fitnessway.data.repository.pending.food

import com.example.fitnessway.data.model.m_26.PendingEdible
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.util.UiStatePager

data class PendingFoodRepositoryUiState(
    override val uiStatePager: UiStatePager<PendingEdible> = UiStatePager()
) : RepositoryPagerState<PendingEdible, PendingFoodRepositoryUiState> {

    override fun copyWithPager(pager: UiStatePager<PendingEdible>): PendingFoodRepositoryUiState =
        copy(uiStatePager = pager)
}