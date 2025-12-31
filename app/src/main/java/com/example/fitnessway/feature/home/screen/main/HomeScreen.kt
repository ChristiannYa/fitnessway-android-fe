package com.example.fitnessway.feature.home.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.home.screen.main.composables.BasicNutrientIntakes
import com.example.fitnessway.feature.home.screen.main.composables.DatePicker
import com.example.fitnessway.feature.home.screen.main.composables.FoodLogs
import com.example.fitnessway.feature.home.screen.main.composables.HomeHeader
import com.example.fitnessway.feature.home.screen.main.composables.OtherNutrientIntakes
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.DarkOverlay
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.Formatters.formatUiErrorMessage
import com.example.fitnessway.util.Ui.handleTempApiErrorMessage
import com.example.fitnessway.util.UiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onViewFoodsList: () -> Unit,
    onViewFoodLogDetails: () -> Unit,
    onNavigateToGoals: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()

    val selectedDate by viewModel.selectedDate.collectAsState()
    val areFoodLogsVisible by viewModel.areFoodLogsVisible.collectAsState()

    val deleteFoodLogErrMsg = handleTempApiErrorMessage(
        uiState = uiState.foodLogDeleteState,
        onTimeOut = viewModel::resetFoodLogDeleteState
    )

    val apiDateFormat = remember(selectedDate) {
        viewModel.getApiFormattedDate()
    }

    // Recompose when either nutrientRepoUiState OR apiDateFormat changes
    val nutrientIntakesState = remember(nutrientRepoUiState, apiDateFormat) {
        nutrientRepoUiState.nutrientIntakesCache[apiDateFormat] ?: UiState.Loading
    }

    val foodLogsState = remember(foodRepoUiState, apiDateFormat) {
        foodRepoUiState.foodLogsCache[apiDateFormat] ?: UiState.Loading
    }

    val pullToRefreshState = rememberPullToRefreshState()
    val pullToRefreshThresholdReached = pullToRefreshState.distanceFraction >= 1f

    val isRefreshing = pullToRefreshThresholdReached &&
            (nutrientRepoUiState.nutrientIntakesCache[apiDateFormat] is UiState.Loading ||
                    foodRepoUiState.foodLogsCache[apiDateFormat] is UiState.Loading)

    LaunchedEffect(key1 = selectedDate) {
        viewModel.loadHomeData()
    }

    DisposableEffect(Unit) {
        onDispose {
            if (areFoodLogsVisible) viewModel.toggleFoodLogsVisibility()
        }
    }

    Screen(
        content = {
            if (viewModel.user != null) {
                val user = viewModel.user

                Box(modifier = Modifier.fillMaxSize()) {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = viewModel::refreshHomeData,
                        state = pullToRefreshState,
                        modifier = Modifier.fillMaxSize(),
                        indicator = {
                            Indicator(
                                modifier = Modifier.align(Alignment.TopCenter),
                                isRefreshing = isRefreshing,
                                state = pullToRefreshState,
                                containerColor = MaterialTheme.colorScheme.surfaceTint,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                HomeHeader(
                                    onToggleFoodLogsVisibility = viewModel::toggleFoodLogsVisibility,
                                    date = viewModel.getFormattedDay(selectedDate)
                                )

                                DatePicker(
                                    onNextDay = { viewModel.changeDay(1) },
                                    onPrevDay = { viewModel.changeDay(-1) }
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                val scrollState = rememberScrollState()

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .verticalScroll(scrollState)
                                ) {
                                    BasicNutrientIntakes(
                                        state = nutrientIntakesState,
                                        user = user,
                                        onNavigateToGoals = onNavigateToGoals
                                    )

                                    OtherNutrientIntakes(
                                        state = nutrientIntakesState,
                                        nutrientType = NutrientType.VITAMIN,
                                        user = user,
                                        onNavigateToGoals = onNavigateToGoals
                                    )

                                    OtherNutrientIntakes(
                                        state = nutrientIntakesState,
                                        nutrientType = NutrientType.MINERAL,
                                        user = user,
                                        onNavigateToGoals = onNavigateToGoals
                                    )

                                    NotFoundMessageAnimated(
                                        isVisible = nutrientIntakesState is UiState.Error,
                                        message = formatUiErrorMessage(nutrientIntakesState),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    FoodLogs(
                        foodLogsState = foodLogsState,
                        onViewFoodsList = { foodLogCategories ->
                            viewModel.setFoodLogCategory(foodLogCategories)
                            onViewFoodsList()
                        },
                        onViewFoodLogDetails = { foodLog ->
                            viewModel.setSelectedFoodLog(foodLog)
                            onViewFoodLogDetails()
                        },
                        onRemoveFoodLog = { foodLog ->
                            viewModel.resetFoodLogDeleteState()
                            viewModel.setSelectedFoodLogToRemove(foodLog)
                            viewModel.deleteFoodLog()
                        },
                        isDeletionError = uiState.foodLogDeleteState is UiState.Error,
                        isVisible = areFoodLogsVisible,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )

                    DarkOverlay(
                        isVisible = areFoodLogsVisible,
                        onClick = viewModel::toggleFoodLogsVisibility
                    )

                    ErrorBannerAnimated(
                        isVisible = deleteFoodLogErrMsg != null,
                        text = deleteFoodLogErrMsg ?: ""
                    )
                }
            } else {
                NotFoundMessage("User not found")
            }
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    FitnesswayTheme {
        HomeScreen(
            onViewFoodsList = {},
            onViewFoodLogDetails = {},
            onNavigateToGoals = {}
        )
    }
}