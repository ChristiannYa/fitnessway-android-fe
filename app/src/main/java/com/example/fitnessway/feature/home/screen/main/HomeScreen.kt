package com.example.fitnessway.feature.home.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.home.screen.main.composables.BasicNutrientIntakes
import com.example.fitnessway.feature.home.screen.main.composables.CreateOptions
import com.example.fitnessway.feature.home.screen.main.composables.DatePicker
import com.example.fitnessway.feature.home.screen.main.composables.FoodLogs
import com.example.fitnessway.feature.home.screen.main.composables.HomeHeader
import com.example.fitnessway.feature.home.screen.main.composables.OtherNutrientIntakes
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessageAnimated
import com.example.fitnessway.ui.shared.BlurOverlay
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.Constants
import com.example.fitnessway.util.Food.combineAll
import com.example.fitnessway.util.Food.getIds
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.Nutrient.combineAll
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

    var headerHeight by remember { mutableStateOf(0.dp) }
    val localDensity = LocalDensity.current

    val deleteFoodLogErrMsg = handleErrStateTempMsg(
        uiState = uiState.foodLogDeleteState,
        onTimeOut = viewModel::resetFoodLogDeleteState
    )

    val apiDateFormat = remember(selectedDate) {
        viewModel.getApiFormattedDate()
    }

    logcat(
        "home screen date: $apiDateFormat",
        Constants.LogLevel.INFO
    )

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

    Screen(
        isMainScreen = true,
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refreshHomeData,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        stickyHeader {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(bottom = if (isCreateMenuVisible) 16.dp else 2.dp)
                                    .onGloballyPositioned { coordinates ->
                                        headerHeight = with(localDensity) {
                                            coordinates.size.height.toDp()
                                        }
                                    },
                                content = {
                                    HomeHeader(
                                        onToggleCreateMenuVisibility = viewModel::toggleCreateMenuVisibility
                                    )

                                    DatePicker(
                                        date = viewModel.getFormattedDay(selectedDate),
                                        goNextDay = { viewModel.changeDay(1) },
                                        goPrevDay = { viewModel.changeDay(-1) }
                                    )

                                    CreateOptions(
                                        onCreateFood = onNavigateToFoodForm,
                                        onCreateSupplement = {},
                                        isVisible = isCreateMenuVisible
                                    )
                                }
                            )
                        }

                        if (viewModel.user == null) {
                            item { NotFoundText("No user found") }
                        } else {
                            val user = viewModel.user

                            if (nutrientIntakesState is UiState.Success) {
                                val firstTwo = nutrientIntakesState.data.combineAll().take(2)

                                logcat("first two nutrient intakes in home screen")
                                firstTwo.forEach {
                                    logcat("> ${it.nutrientWithPreferences.nutrient.name} ${it.amount}")
                                }
                            }

                            item {
                                BasicNutrientIntakes(
                                    state = nutrientIntakesState,
                                    user = user,
                                    onNavigateToGoals = onNavigateToGoals
                                )
                            }

                            item {
                                OtherNutrientIntakes(
                                    state = nutrientIntakesState,
                                    nutrientType = NutrientType.VITAMIN,
                                    user = user,
                                    onNavigateToGoals = onNavigateToGoals
                                )
                            }

                            item {
                                OtherNutrientIntakes(
                                    state = nutrientIntakesState,
                                    nutrientType = NutrientType.MINERAL,
                                    user = user,
                                    onNavigateToGoals = onNavigateToGoals
                                )
                            }

                            if (foodLogsState is UiState.Success) {
                                val foodLogIds = foodLogsState.data.combineAll().getIds()
                                logcat("food log ids $foodLogIds")
                            }

                            item {
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
                            }
                        }
                    }
                }

                BlurOverlay(
                    isVisible = isCreateMenuVisible,
                    onClick = viewModel::toggleCreateMenuVisibility,
                    modifier = Modifier.padding(top = headerHeight),
                )

                ApiErrorMessageAnimated(
                    isVisible = deleteFoodLogErrMsg != "",
                    errorMessage = deleteFoodLogErrMsg
                )
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
            onNavigateToFoodForm = {},
            onNavigateToGoals = {}
        )
    }
}