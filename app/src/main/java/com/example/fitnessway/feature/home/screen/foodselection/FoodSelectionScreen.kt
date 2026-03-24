package com.example.fitnessway.feature.home.screen.foodselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.home.screen.foodselection.composables.SearchAppFood
import com.example.fitnessway.feature.home.screen.foodselection.composables.UserFoodsList
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.util.UFood.Ui.getFoodLogCategory
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodSelectionScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onNavigateToSelectedFood: () -> Unit,
) {
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val foodLogCategory by viewModel.foodLogCategory.collectAsState()

    val user = userFlow
    val foodsUiState = foodRepoUiState.foodsUiState

    val searchResults = emptyList<String>()

    LaunchedEffect(Unit) {
        viewModel.getFoods()
    }

    if (user != null) {
        val foodLogCategoryCopy = foodLogCategory

        if (foodLogCategoryCopy != null) {
            val categoryString = getFoodLogCategory(foodLogCategoryCopy)

            Screen(
                header = {
                    Header(
                        onBackClick = onBackClick,
                        title = "$categoryString selection",
                    )
                },
            ) { focusManager ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SearchAppFood(
                        onSearch = { query ->
                            focusManager.clearFocus()
                        },
                        searchResults = searchResults
                    )

                    UserFoodsList(
                        foodsUiState = foodsUiState,
                        isUserPremium = user.isPremium,
                        onRefresh = viewModel::refreshFoodSelectionScreenData,
                        onFoodClick = {
                            viewModel.setSelectedFoodToLog(it)
                            onNavigateToSelectedFood()
                        }
                    )
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