package com.example.fitnessway.data.repository.edible_recent_log.supplement

import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.util.UiStatePager

data class SupplementRecentLogRepositoryUiState(
    override val uiStatePager: UiStatePager<FoodPreview> = UiStatePager()
) : RepositoryPagerState<FoodPreview, SupplementRecentLogRepositoryUiState> {

    override fun copyWithPager(pager: UiStatePager<FoodPreview>): SupplementRecentLogRepositoryUiState =
        copy(uiStatePager = pager)
}