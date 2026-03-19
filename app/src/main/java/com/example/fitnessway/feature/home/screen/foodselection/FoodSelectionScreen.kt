package com.example.fitnessway.feature.home.screen.foodselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.feature.home.screen.foodselection.composables.SearchAppFood
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure.AppIconButtonSource
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.util.Formatters.formatUiErrorMessage
import com.example.fitnessway.util.UFood.Ui.foodsListWithState
import com.example.fitnessway.util.UFood.Ui.getFoodLogCategory
import com.example.fitnessway.util.UiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodSelectionScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onNavigateToSelectedFood: () -> Unit,
    onNavigateToFoodRequest: () -> Unit,
) {
    val userFlow by viewModel.userFlow.collectAsState()
    val foodLogCategory by viewModel.foodLogCategory.collectAsState()

    val user = userFlow

    LaunchedEffect(Unit) {
        viewModel.getFoods()
    }

    val pullToRefreshState = rememberPullToRefreshState()
    val pullToRefreshThresholdReached = pullToRefreshState.distanceFraction >= 1f
    val isRefreshing = pullToRefreshThresholdReached

    val searchResults = emptyList<String>()
    val userFoodsToggled by remember { mutableStateOf(false) }

    if (user != null) {
        val foodLogCategoryCopy = foodLogCategory

        if (foodLogCategoryCopy != null) {
            val categoryString = getFoodLogCategory(foodLogCategoryCopy)

            Screen(
                header = {
                    Header(
                        onBackClick = onBackClick,
                        title = "$categoryString selection",
                    ) {
                        Clickables.AppPngIconButton(
                            icon = AppIconButtonSource.Vector(Icons.Default.Info),
                            contentDescription = "Not found what you're looking for?",
                            enabled = true,
                            onClick = onNavigateToFoodRequest
                        )

                        Clickables.AppPngIconButton(
                            onClick = {},
                            contentDescription = "View My Foods",
                            icon = AppIconButtonSource.Vector(Icons.Default.ClearAll)
                        )
                    }
                },
            ) { focusManager ->
                Box(modifier = Modifier.fillMaxSize()) {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        state = pullToRefreshState,
                        onRefresh = viewModel::refreshFoodSelectionScreenData,
                        indicator = {
                            Loading.RefreshByPullIndicator(
                                isRefreshing = isRefreshing,
                                state = pullToRefreshState,
                                modifier = Modifier.align(Alignment.TopCenter)
                            )
                        }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box {
                                SearchAppFood(
                                    onSearch = { query ->
                                        focusManager.clearFocus()
                                    },
                                    searchResults = searchResults,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                }
            }

        } else NotFoundScreen(
            onBackClick = onBackClick,
            message = "Food log category not found"
        )

    } else NotFoundScreen(
        onBackClick = onBackClick,
        message = "User not found"
    )
}

@Composable
private fun Foods(
    state: UiState<List<FoodInformation>>,
    onFoodClick: (FoodInformation) -> Unit,
    isUserPremium: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxHeight()
    ) {
        foodsListWithState(
            state = state,
            isUserPremium = isUserPremium,
            showsNutrientPreview = true,
            onFoodClick = onFoodClick
        )
    }

    NotFoundMessageAnimated(
        isVisible = state is UiState.Error,
        message = formatUiErrorMessage(state)
    )
}