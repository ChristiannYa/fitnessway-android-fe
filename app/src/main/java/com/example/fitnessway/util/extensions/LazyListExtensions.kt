package com.example.fitnessway.util.extensions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun LazyListState.OnLoadMore(onLoadMore: () -> Unit) {
    LaunchedEffect(this) {
        snapshotFlow {
            val viewportEndOffset = layoutInfo.viewportEndOffset
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastItem = layoutInfo.visibleItemsInfo.lastOrNull()

            lastItem?.let {
                val isLastItem = lastItem.index == totalItemsCount - 1
                val lastItemYBottomCord = lastItem.offset + lastItem.size
                val isFullyVisible = lastItemYBottomCord <= viewportEndOffset

                totalItemsCount > 0 && isLastItem && isFullyVisible
            } ?: false
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadMore() }
    }
}