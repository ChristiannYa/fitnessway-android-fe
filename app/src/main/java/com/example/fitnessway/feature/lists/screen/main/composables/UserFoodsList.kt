package com.example.fitnessway.feature.lists.screen.main.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.UFood
import com.example.fitnessway.util.UiState

@Composable
fun UserFoodsList(
    isVisible: Boolean,
    isUserPremium: Boolean,
    foodsUiState: UiState<List<MFood.Model.FoodInformation>>,
    onRefresh: () -> Unit,
    onFoodClick: (MFood.Model.FoodInformation) -> Unit,
    modifier: Modifier = Modifier
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val pullToRefreshThresholdReached = pullToRefreshState.distanceFraction >= 1f
    val isRefreshing = pullToRefreshThresholdReached && foodsUiState is UiState.Loading

    AnimatedVisibility(
        visible = isVisible,
        enter = Animation.ComposableTransition.fadeIn,
        exit = Animation.ComposableTransition.fadeOut,
        modifier = modifier
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            state = pullToRefreshState,
            onRefresh = onRefresh,
            indicator = {
                Loading.RefreshByPullIndicator(
                    isRefreshing = isRefreshing,
                    state = pullToRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            UFood.Ui.UserFoodsList(
                state = foodsUiState,
                isUserPremium = isUserPremium,
                showsNutrientPreview = true,
                onFoodClick = onFoodClick
            )
        }
    }
}