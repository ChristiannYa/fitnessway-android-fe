package com.example.fitnessway.data.repository.edible_recent_log.food

import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.util.UiStatePager

data class FoodRecentLogUiState(
    override val uiStatePager: UiStatePager<FoodPreview> = UiStatePager()
) : RepositoryPagerState<FoodPreview, FoodRecentLogUiState> {

    override fun copyWithPager(pager: UiStatePager<FoodPreview>): FoodRecentLogUiState =
        copy(uiStatePager = pager)
}