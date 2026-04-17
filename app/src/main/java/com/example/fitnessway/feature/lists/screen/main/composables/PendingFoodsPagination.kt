package com.example.fitnessway.feature.lists.screen.main.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toErrorMessageOrNull
import com.example.fitnessway.data.mappers.toPaginationOrNull
import com.example.fitnessway.data.mappers.toPascalSpaced
import com.example.fitnessway.data.mappers.toPreview
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.shared.Messages
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.UFood
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import com.example.fitnessway.util.extensions.OnLoadMore
import com.example.fitnessway.util.extensions.getAccent
import com.example.fitnessway.util.extensions.getImageVector

@Composable
fun PendingFoodsPagination(
    isVisible: Boolean,
    isUserPremium: Boolean,
    isDismissError: Boolean,
    uiStatePager: UiStatePager<PendingFood>,
    onLoadMore: () -> Unit,
    onFoodClick: (PendingFood) -> Unit,
    onDismissReview: (Int) -> Unit,
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
            when (uiStatePager.uiState) {
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
                        uiStatePager.toPaginationOrNull()?.let { pagination ->
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
                                    FoodPreview(
                                        pendingFood = pendingFood,
                                        isDismissError = isDismissError,
                                        isUserPremium = isUserPremium,
                                        onClick = onFoodClick,
                                        onDismiss = onDismissReview,
                                        modifier = Modifier.fillMaxWidth()
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
                isVisible = uiStatePager.uiState is UiState.Error,
                message = uiStatePager.uiState.toErrorMessageOrNull() ?: ""
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodPreview(
    pendingFood: PendingFood,
    isDismissError: Boolean,
    isUserPremium: Boolean,
    onClick: (PendingFood) -> Unit,
    onDismiss: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold

    val swipeToDismissState = remember {
        SwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            positionalThreshold = positionalThreshold
        )
    }

    LaunchedEffect(swipeToDismissState.settledValue) {
        if (swipeToDismissState.settledValue == SwipeToDismissBoxValue.EndToStart) {
            onDismiss(pendingFood.id)
        }
    }

    LaunchedEffect(isDismissError) {
        if (isDismissError) swipeToDismissState.reset()
    }

    SwipeToDismissBox(
        state = swipeToDismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = pendingFood.status.isReviewed,
        modifier = modifier,
        backgroundContent = {
            if (swipeToDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.CenterEnd)
                ) {
                    Text(
                        text = "Dismiss",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) {
        UFood.Ui.FoodPreview(
            food = pendingFood.toPreview(),
            isUserPremium = isUserPremium,
            showsNutrientPreview = true,
            contentRight = {
                Structure.AppIconDynamic(
                    source = Structure.AppIconSource.Vector(
                        pendingFood.status.getImageVector()
                    ),
                    contentDescription = "Food is ${pendingFood.status.name.toPascalSpaced()}",
                    tint = pendingFood.status.getAccent()
                )
            },
            onClick = { onClick(pendingFood) }
        )
    }
}