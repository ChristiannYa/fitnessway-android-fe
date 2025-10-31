package com.example.fitnessway.feature.home.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.home.screen.main.composables.BasicNutrientIntakes
import com.example.fitnessway.feature.home.screen.main.composables.DatePicker
import com.example.fitnessway.feature.home.screen.main.composables.FoodLogs
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onFoodLog: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    LaunchedEffect(key1 = selectedDate) {
        viewModel.getNutrientIntakes()
        viewModel.getFoodLogs()
    }

    Screen(
        isMainScreen = true,
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = {
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
                        item {
                            BasicNutrientIntakes(
                                uiState.nutrientIntakesState,
                                viewModel.user
                            )
                        }

                        item {
                            FoodLogs(
                                uiState.foodLogsState,
                                onFoodLog,
                                setFoodLogCategory = viewModel::setFoodLogCategory
                            )
                        }
                    }
                }
            )
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    FitnesswayTheme {
        HomeScreen(onFoodLog = {})
    }
}