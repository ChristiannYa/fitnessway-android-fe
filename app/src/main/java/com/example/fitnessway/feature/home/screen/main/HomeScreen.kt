package com.example.fitnessway.feature.home.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.fitnessway.ui.shared.ApiErrorBanner
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onViewFoodsList: () -> Unit,
    onViewFoodLogDetails: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    LaunchedEffect(key1 = selectedDate) {
        viewModel.getNutrientIntakes()
        viewModel.getFoodLogs()
    }

    val deleteFoodLogErrMsg = when (val state = uiState.foodLogDeleteState) {
        is UiState.Error -> {
            LaunchedEffect(state) {
                delay(10000)
                viewModel.resetFoodLogDeleteState()
            }

            state.message
        }

        else -> null
    }

    Screen(
        isMainScreen = true,
        content = {
            Box(
                modifier = Modifier.fillMaxSize(),
                content = {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxHeight(),
                        content = {
                            item {
                                HomeHeader(
                                    onToggleCreateMenuVisibility = {
                                        viewModel.toggleCreateMenuVisibility()
                                    }
                                )
                            }

                            item {
                                CreateOptions(
                                    onCreateFood = {},
                                    onCreateSupplement = {}
                                )
                            }

                            item {
                                DatePicker(
                                    date = viewModel.getFormattedDay(selectedDate),
                                    goNextDay = { viewModel.changeDay(1) },
                                    goPrevDay = { viewModel.changeDay(-1) }
                                )
                            }

                            if (viewModel.user == null) {
                                item { Text("No user found") }
                            } else {
                                val nutrientsState = uiState.nutrientIntakesState
                                val user = viewModel.user

                                item {
                                    BasicNutrientIntakes(nutrientsState, user)
                                }

                                item {
                                    OtherNutrientIntakes(
                                        state = nutrientsState,
                                        nutrientType = NutrientType.VITAMIN,
                                        user = user
                                    )
                                }

                                item {
                                    OtherNutrientIntakes(
                                        state = nutrientsState,
                                        nutrientType = NutrientType.MINERAL,
                                        user = user
                                    )
                                }

                                item {
                                    FoodLogs(
                                        foodLogsState = uiState.foodLogsState,
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
                    )

                    ApiErrorBanner(
                        message = deleteFoodLogErrMsg,
                        onDismiss = { viewModel.resetFoodLogDeleteState() },
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            )
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    FitnesswayTheme {
        HomeScreen(
            onViewFoodsList = {},
            onViewFoodLogDetails = {}
        )
    }
}