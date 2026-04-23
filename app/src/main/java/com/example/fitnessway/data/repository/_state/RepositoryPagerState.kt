package com.example.fitnessway.data.repository._state

import com.example.fitnessway.util.UiStatePager

interface RepositoryPagerState<T, S : RepositoryPagerState<T, S>> {
    val uiStatePager: UiStatePager<T>

    fun copyWithPager(pager: UiStatePager<T>): S
}