package com.example.fitnessway.data.repository.edible_list.food

import com.example.fitnessway.data.model.m_26.UserEdible
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.util.UiStatePager

data class UserFoodRepositoryUiState(
    override val uiStatePager: UiStatePager<UserEdible> = UiStatePager()
) : RepositoryPagerState<UserEdible, UserFoodRepositoryUiState> {

    override fun copyWithPager(pager: UiStatePager<UserEdible>): UserFoodRepositoryUiState =
        copy(uiStatePager = pager)
}