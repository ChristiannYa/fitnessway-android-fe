package com.example.fitnessway.data.repository._state

import com.example.fitnessway.data.mappers.toPaginationOrNull
import com.example.fitnessway.data.model.m_26.PaginationResult
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

fun <T, S : RepositoryPagerState<T, S>> RepositoryPagerState<T, S>.loadMore(
    uiState: MutableStateFlow<S>,
    fetch: (offset: Long) -> Flow<UiState<PaginationResult<T>>>,
    scope: CoroutineScope
) {
    val pager = this.uiStatePager
    if (pager.isLoadingMore) return

    val pagination = pager.toPaginationOrNull() ?: return
    if (!pagination.hasMorePages) return

    uiState.update { it.copyWithPager(pager.copy(isLoadingMore = true)) }

    scope.launch {
        fetch(pagination.getNextOffset()).collect { state ->
            when (state) {
                is UiState.Success -> uiState.update {
                    val current = it.uiStatePager.toPaginationOrNull()
                    val accumulated = (current?.data ?: emptyList()) + state.data.data

                    it.copyWithPager(
                        pager = UiStatePager(
                            uiState = UiState.Success(
                                data = state.data.copy(data = accumulated)
                            ),
                            isLoadingMore = false
                        )
                    )
                }

                is UiState.Error -> uiState.update {
                    it.copyWithPager(
                        pager = it.uiStatePager.copy(
                            isLoadingMore = false
                        )
                    )
                }

                else -> {}
            }
        }
    }
}