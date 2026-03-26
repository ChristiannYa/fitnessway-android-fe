package com.example.fitnessway.feature.home.screen.foodselection.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toErrorMessageOrNull
import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.util.UFood
import com.example.fitnessway.util.UiState

@Composable
fun UserFoodsList(
    foodsUiState: UiState<List<MFood.Model.FoodInformation>>,
    isUserPremium: Boolean,
    onRefresh: () -> Unit,
    onFoodClick: (MFood.Model.FoodInformation) -> Unit,
    modifier: Modifier = Modifier
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val pullToRefreshThresholdReached = pullToRefreshState.distanceFraction >= 1f
    val isRefreshing = pullToRefreshThresholdReached && foodsUiState is UiState.Loading

    Box(modifier = modifier) {
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
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                NotFoundMessageAnimated(
                    isVisible = foodsUiState is UiState.Error,
                    message = foodsUiState.toErrorMessageOrNull() ?: ""
                )

                UFood.Ui.UserFoodsList(
                    state = foodsUiState,
                    isUserPremium = isUserPremium,
                    showsNutrientPreview = true,
                    onFoodClick = onFoodClick
                )
            }
        }
    }
}