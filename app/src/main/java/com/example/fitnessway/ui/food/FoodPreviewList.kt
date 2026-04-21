package com.example.fitnessway.ui.food

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toPaginationOrNull
import com.example.fitnessway.data.mappers.toPreview
import com.example.fitnessway.data.model.m_26.UserEdible
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.shared.Messages
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.UFood
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import com.example.fitnessway.util.extensions.OnLoadMore

@Composable
fun FoodPreviewList(
    uiStatePager: UiStatePager<UserEdible>,
    isVisible: Boolean,
    isUserPremium: Boolean,
    onLoadMore: () -> Unit,
    onFoodClick: (UserEdible) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()

    lazyListState.OnLoadMore(onLoadMore)

    AnimatedVisibility(
        visible = isVisible,
        enter = Animation.ComposableTransition.fadeIn,
        exit = Animation.ComposableTransition.fadeOut,
        modifier = modifier
    ) {
        Box(modifier.fillMaxHeight()) {
            when (uiStatePager.uiState) {
                is UiState.Loading -> Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(12) {
                        Loading.Composable(height = 32.dp)
                    }
                }

                is UiState.Success -> LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    uiStatePager.toPaginationOrNull()?.let { pagination ->
                        val data = pagination.data

                        if (data.isEmpty()) {
                            item {
                                Messages.NotFoundMessage("Foods that you add to your list will appear here")
                            }
                        } else {
                            items(
                                items = data,
                                key = { it.id }
                            ) {
                                val preview = it.toPreview()

                                UFood.Ui.FoodPreview(
                                    food = preview,
                                    isUserPremium = isUserPremium,
                                    showsNutrientPreview = true,
                                    onClick = { onFoodClick(it) }
                                )
                            }

                            if (pagination.hasMorePages) {
                                item {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        CircularProgressIndicator(
                                            strokeWidth = Ui.Measurements.LOADING_CIRCLE_IN_SCREEN_STROKE_WIDTH,
                                            modifier = Modifier.size(Ui.Measurements.LOADING_CIRCLE_IN_SCREEN_SIZE)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}