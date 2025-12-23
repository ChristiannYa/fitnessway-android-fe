package com.example.fitnessway.feature.home.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.fitnessway.feature.home.screen.main.composables.HomeHeader
import com.example.fitnessway.feature.home.screen.main.composables.OtherNutrientIntakes
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessageAnimated
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.Ui.handleErrStateTempMsg
import com.example.fitnessway.util.UiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onViewFoodsList: () -> Unit,
    onViewFoodLogDetails: () -> Unit,
    onNavigateToFoodForm: () -> Unit,
    onNavigateToGoals: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()

    val selectedDate by viewModel.selectedDate.collectAsState()
    val isCreateMenuVisible by viewModel.isCreateMenuVisible.collectAsState()

    val deleteFoodLogErrMsg = handleErrStateTempMsg(
        uiState = uiState.foodLogDeleteState,
        onTimeOut = viewModel::resetFoodLogDeleteState
    )

    val apiDateFormat = remember(selectedDate) {
        viewModel.getApiFormattedDate()
    }

    val isRefreshing = nutrientRepoUiState.nutrientIntakesCache[apiDateFormat] is UiState.Loading ||
            foodRepoUiState.foodLogsCache[apiDateFormat] is UiState.Loading

    LaunchedEffect(key1 = selectedDate) {
        viewModel.loadHomeData()
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isCreateMenuVisible) viewModel.toggleCreateMenuVisibility()
        }
    }

    // Recompose when either nutrientRepoUiState OR apiDateFormat changes
    val nutrientIntakesState = remember(nutrientRepoUiState, apiDateFormat) {
        nutrientRepoUiState.nutrientIntakesCache[apiDateFormat] ?: UiState.Loading
    }

    val foodLogsState = remember(foodRepoUiState, apiDateFormat) {
        foodRepoUiState.foodLogsCache[apiDateFormat] ?: UiState.Loading
    }

    val pullToRefreshState = rememberPullToRefreshState()

    Screen(
        isMainScreen = true,
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
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        val scrollState = rememberScrollState()

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {

                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                HomeHeader(
                                    onToggleCreateMenuVisibility = viewModel::toggleCreateMenuVisibility
                                )

                                DatePicker(
                                    date = viewModel.getFormattedDay(selectedDate),
                                    onNextDay = { viewModel.changeDay(1) },
                                    onPrevDay = { viewModel.changeDay(-1) }
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.weight(1f)
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
                            }
                        }
                    }

                    ApiErrorMessageAnimated(
                        isVisible = deleteFoodLogErrMsg != "",
                        errorMessage = deleteFoodLogErrMsg
                    )
                }
            } else {
                NotFoundText("User not found")
            }
        }
    )
}

/*
FoodLogs(
    foodLogsState = foodLogsState,
    foodLogDeleteState = uiState.foodLogDeleteState,
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
    }
)
 */

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    FitnesswayTheme {
        HomeScreen(
            onViewFoodsList = {},
            onViewFoodLogDetails = {},
            onNavigateToFoodForm = {},
            onNavigateToGoals = {}
        )
    }
}