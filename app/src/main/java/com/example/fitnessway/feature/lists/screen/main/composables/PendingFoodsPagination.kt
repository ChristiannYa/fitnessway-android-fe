package com.example.fitnessway.feature.lists.screen.main.composables

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
import com.example.fitnessway.data.mappers.toErrorMessageOrNull
import com.example.fitnessway.data.mappers.toPaginationOrNull
import com.example.fitnessway.data.mappers.toPreview
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.shared.Messages
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.UFood
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import com.example.fitnessway.util.extensions.OnLoadMore

@Composable
fun PendingFoodsPagination(
    isVisible: Boolean,
    isUserPremium: Boolean,
    pendingFoodsUiStatePager: UiStatePager<PendingFood>,
    onLoadMore: () -> Unit,
    onFoodClick: (PendingFood) -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingFoodsListLazyState = rememberLazyListState()

    pendingFoodsListLazyState.OnLoadMore(onLoadMore)

    AnimatedVisibility(
        visible = isVisible,
        enter = Animation.ComposableTransition.fadeIn,
        exit = Animation.ComposableTransition.fadeOut,
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxHeight()) {
            when (pendingFoodsUiStatePager.uiState) {
                is UiState.Loading -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        repeat(12) {
                            Loading.Composable(height = 32.dp)
                        }
                    }
                }

                is UiState.Success -> {
                    LazyColumn(
                        state = pendingFoodsListLazyState,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        pendingFoodsUiStatePager.toPaginationOrNull()?.let { pagination ->
                            val pendingFoods = pagination.data

                            if (pendingFoods.isEmpty()) {
                                item {
                                    Messages.NotFoundMessage(
                                        "Foods that you'd like submit to the app will appear here"
                                    )
                                }
                            } else {
                                items(
                                    items = pendingFoods,
                                    key = { it.id }
                                ) { pendingFood ->
                                    UFood.Ui.FoodPreview(
                                        food = pendingFood.toPreview(),
                                        isUserPremium = isUserPremium,
                                        showsNutrientPreview = true,
                                        onClick = { onFoodClick(pendingFood) }
                                    )
                                }

                                if (pagination.hasMorePages) {
                                    item {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .size(
                                                        Ui.Measurements.LOADING_CIRCLE_IN_HEADER_SIZE
                                                    ),
                                                strokeWidth = Ui.Measurements.LOADING_CIRCLE_IN_HEADER_STROKE_WIDTH
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {}
            }

            Messages.NotFoundMessageAnimated(
                isVisible = pendingFoodsUiStatePager.uiState is UiState.Error,
                message = pendingFoodsUiStatePager.uiState.toErrorMessageOrNull() ?: ""
            )
        }
    }
}