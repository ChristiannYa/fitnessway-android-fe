package com.example.fitnessway.data.repository.edible_recent_log.supplement

import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.util.UiStatePager

data class SupplementRecentLogUiState(
    override val uiStatePager: UiStatePager<FoodPreview> = UiStatePager()
) : RepositoryPagerState<FoodPreview, SupplementRecentLogUiState> {

    override fun copyWithPager(pager: UiStatePager<FoodPreview>): SupplementRecentLogUiState =
        copy(uiStatePager = pager)
}