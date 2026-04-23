package com.example.fitnessway.data.repository.user_supplement

import com.example.fitnessway.data.model.m_26.UserEdible
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.util.UiStatePager

data class UserSupplementRepositoryUiState(
    override val uiStatePager: UiStatePager<UserEdible> = UiStatePager()
) : RepositoryPagerState<UserEdible, UserSupplementRepositoryUiState> {

    override fun copyWithPager(pager: UiStatePager<UserEdible>) = copy(uiStatePager = pager)
}