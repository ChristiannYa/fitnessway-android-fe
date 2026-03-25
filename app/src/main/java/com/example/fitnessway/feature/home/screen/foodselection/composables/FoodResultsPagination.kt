package com.example.fitnessway.feature.home.screen.foodselection.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toErrorMessageOrNull
import com.example.fitnessway.data.mappers.toPaginationOrNull
import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.data.model.m_26.FoodSearchResult
import com.example.fitnessway.ui.FoodPreviewCard
import com.example.fitnessway.ui.shared.Messages
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import com.example.fitnessway.util.extensions.OnLoadMore

@Composable
fun FoodResultsPagination(
    isTyping: Boolean,
    isUserPremium: Boolean,
    appFoodsUiStatePager: UiStatePager<FoodSearchResult>,
    onTypingConsumed: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = isTyping || appFoodsUiStatePager.uiState is UiState.Loading

    val appFoodsLazyListState = rememberLazyListState()

    appFoodsLazyListState.OnLoadMore(onLoadMore)

    LaunchedEffect(appFoodsUiStatePager.uiState) {
        if (appFoodsUiStatePager.uiState.hasFetched) {
            onTypingConsumed()
        }
    }

    Box(
        modifier = modifier
            .animateContentSize()
            .then(
                if (isLoading) {
                    Modifier.fillMaxHeight()
                } else Modifier
            )
    ) {
        if (isLoading) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    strokeWidth = Ui.Measurements.LOADING_CIRCLE_IN_SCREEN_STROKE_WIDTH,
                    modifier = Modifier
                        .size(
                            Ui.Measurements.LOADING_CIRCLE_IN_SCREEN_SIZE
                        )
                )
            }
        }

        when (appFoodsUiStatePager.uiState) {
            is UiState.Success -> {
                LazyColumn(
                    state = appFoodsLazyListState,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    appFoodsUiStatePager.toPaginationOrNull()?.let { pagination ->
                        val appFoods = pagination.data

                        if (appFoods.isEmpty()) {
                            item {
                                Messages.NotFoundMessage("No results")
                            }
                        } else {
                            items(
                                items = appFoods,
                                key = { it.id }
                            ) { pendingFood ->
                                FoodPreviewCard(
                                    preview = FoodPreview(
                                        id = pendingFood.id,
                                        base = pendingFood.base,
                                        nutrientPreview = pendingFood.nutrientsPreview,
                                        isUserPremium = isUserPremium
                                    )
                                )
                            }
                        }
                    }
                }
            }

            else -> {}
        }

        Messages.NotFoundMessageAnimated(
            isVisible = appFoodsUiStatePager.uiState is UiState.Error,
            message = appFoodsUiStatePager.uiState.toErrorMessageOrNull() ?: ""
        )
    }
}