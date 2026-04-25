package com.example.fitnessway.data.repository.edible_list.supplement

import com.example.fitnessway.data.model.m_26.UserEdible
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.util.UiStatePager

data class UserSupplementUiState(
    override val uiStatePager: UiStatePager<UserEdible> = UiStatePager()
) : RepositoryPagerState<UserEdible, UserSupplementUiState> {

    override fun copyWithPager(pager: UiStatePager<UserEdible>): UserSupplementUiState =
        copy(uiStatePager = pager)
}