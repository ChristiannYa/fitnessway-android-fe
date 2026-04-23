package com.example.fitnessway.data.repository.pending_food

import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.util.UiStatePager

data class PendingFoodRepositoryUiState(
    override val uiStatePager: UiStatePager<PendingFood> = UiStatePager()
) : RepositoryPagerState<PendingFood, PendingFoodRepositoryUiState> {

    override fun copyWithPager(pager: UiStatePager<PendingFood>) = copy(uiStatePager = pager)
}