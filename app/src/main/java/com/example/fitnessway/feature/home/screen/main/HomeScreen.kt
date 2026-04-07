package com.example.fitnessway.feature.home.screen.main

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toErrorMessageOrNull
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.feature.home.screen.main.composables.BasicNutrientIntakes
import com.example.fitnessway.feature.home.screen.main.composables.DatePicker
import com.example.fitnessway.feature.home.screen.main.composables.FoodLogs
import com.example.fitnessway.feature.home.screen.main.composables.HomeHeader
import com.example.fitnessway.feature.home.screen.main.composables.OtherNutrientIntakes
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.DarkOverlay
import com.example.fitnessway.ui.shared.Loading.RefreshByPullIndicator
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Ui.handleTempApiErrMsg
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
    val userFlow by viewModel.userFlow.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val foodLogRepoUiState by viewModel.foodLogRepoUiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val areFoodLogsVisible by viewModel.areFoodLogsVisible.collectAsState()

    val user = userFlow

    val deleteFoodLogErrMsg = handleTempApiErrMsg(
        uiState = uiState.foodLogDeleteState,
        onTimeOut = viewModel::resetFoodLogDeleteState
    )

    val apiDateFormat = remember(selectedDate) {
        viewModel.dateTimeFormatter.formatKebabDate(selectedDate)
    }

    // Recompose when either nutrientRepoUiState OR apiDateFormat changes
    val nutrientIntakesState = remember(nutrientRepoUiState, apiDateFormat) {
        nutrientRepoUiState.nutrientIntakesCache[apiDateFormat] ?: UiState.Loading
    }

    val foodLogsState = remember(foodLogRepoUiState, apiDateFormat) {
        foodLogRepoUiState.foodLogs[apiDateFormat] ?: UiState.Loading

    }

    val pullToRefreshState = rememberPullToRefreshState()
    val pullToRefreshThresholdReached = pullToRefreshState.distanceFraction >= 1f

    val isRefreshing = pullToRefreshThresholdReached &&
            (nutrientRepoUiState.nutrientIntakesCache[apiDateFormat] is UiState.Loading ||
                    foodLogRepoUiState.foodLogs[apiDateFormat] is UiState.Loading)

    LaunchedEffect(key1 = selectedDate) {
        viewModel.loadHomeData()
    }

    DisposableEffect(Unit) {
        onDispose {
            if (areFoodLogsVisible) viewModel.toggleFoodLogsVisibility()
        }
    }

    val view = LocalView.current

    Screen {
        if (user != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refreshHomeData,
                    state = pullToRefreshState,
                    modifier = Modifier.fillMaxSize(),
                    indicator = {
                        RefreshByPullIndicator(
                            isRefreshing = isRefreshing,
                            state = pullToRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            HomeHeader(
                                onToggleFoodLogsVisibility = {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    viewModel.toggleFoodLogsVisibility()
                                },
                                date = viewModel.dateTimeFormatter.getDayDisplay(selectedDate)
                            )

                            DatePicker(
                                onNextDay = {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    viewModel.changeDay(1)
                                },
                                onPrevDay = {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    viewModel.changeDay(-1)
                                }
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
                                    isUserPremium = user.isPremium,
                                    onNavigateToGoals = onNavigateToGoals
                                )

                                OtherNutrientIntakes(
                                    state = nutrientIntakesState,
                                    nutrientType = NutrientType.VITAMIN,
                                    isUserPremium = user.isPremium,
                                    onNavigateToGoals = onNavigateToGoals
                                )

                                OtherNutrientIntakes(
                                    state = nutrientIntakesState,
                                    nutrientType = NutrientType.MINERAL,
                                    isUserPremium = user.isPremium,
                                    onNavigateToGoals = onNavigateToGoals
                                )

                                NotFoundMessageAnimated(
                                    isVisible = nutrientIntakesState is UiState.Error,
                                    message = nutrientIntakesState.toErrorMessageOrNull() ?: "",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                FoodLogs(
                    foodLogsState = foodLogsState,
                    isVisible = areFoodLogsVisible,
                    isDeletionError = uiState.foodLogDeleteState is UiState.Error,
                    formatTime = viewModel.dateTimeFormatter::formatTime,
                    onViewFoodsList = { foodLogCategories ->
                        view.playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT)
                        viewModel.setFoodLogCategory(foodLogCategories)
                        onViewFoodsList()
                    },
                    onViewFoodLogDetails = { foodLog ->
                        view.playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT)
                        viewModel.setSelectedFoodLog(foodLog)
                        onViewFoodLogDetails()
                    },
                    onRemoveFoodLog = { foodLog ->
                        viewModel.resetFoodLogDeleteState()
                        viewModel.setSelectedFoodLogToRemove(foodLog)
                        viewModel.deleteFoodLog()
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )

                DarkOverlay(
                    isVisible = areFoodLogsVisible,
                    onClick = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        viewModel.toggleFoodLogsVisibility()
                    }
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
}