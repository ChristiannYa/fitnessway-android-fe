package com.example.fitnessway.feature.home.screen.foodselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.home.screen.foodselection.composables.AppFoodSearchBar
import com.example.fitnessway.feature.home.screen.foodselection.composables.FoodResultsPagination
import com.example.fitnessway.feature.home.screen.foodselection.composables.UserFoodsList
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.util.toPascalCaseSpaced
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodSelectionScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onNavigateToSelectedFood: () -> Unit,
) {
    val appFoodRepoUiState by viewModel.appFoodRepoUiState.collectAsState()
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val foodLogCategory by viewModel.foodLogCategory.collectAsState()

    val user = userFlow
    val appFoodsUiStatePager = appFoodRepoUiState.appFoodsUiStatePager
    val foodsUiState = foodRepoUiState.foodsUiState

    LaunchedEffect(Unit) {
        viewModel.getFoods()
    }

    if (user != null) {
        val foodLogCategoryCopy = foodLogCategory

        if (foodLogCategoryCopy != null) {
            val categoryString = foodLogCategoryCopy.name.toPascalCaseSpaced()

            Screen(
                header = {
                    Header(
                        onBackClick = onBackClick,
                        title = "$categoryString selection",
                    )
                },
            ) { focusManager ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    var appFoodSearchQuery by remember { mutableStateOf("") }
                    var isTyping by remember { mutableStateOf(false) }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        AppFoodSearchBar(
                            query = appFoodSearchQuery,
                            onQueryChange = { q ->
                                appFoodSearchQuery = q
                                isTyping = q.isNotBlank()
                                viewModel.searchAppFoods(q)
                            },
                            focusManager = focusManager
                        )

                        FoodResultsPagination(
                            isTyping = isTyping,
                            isUserPremium = user.isPremium,
                            appFoodsUiStatePager = appFoodsUiStatePager,
                            onTypingConsumed = { isTyping = false },
                            onLoadMore = { viewModel.loadMoreAppFoods(appFoodSearchQuery) }
                        )
                    }

                    UserFoodsList(
                        foodsUiState = foodsUiState,
                        isUserPremium = user.isPremium,
                        onRefresh = viewModel::refreshFoodSelectionScreenData,
                        onFoodClick = {
                            viewModel.setSelectedFoodToLog(it)
                            onNavigateToSelectedFood()
                        },
                        modifier = Modifier
                            .then(
                                if (!appFoodSearchQuery.isNotBlank()) {
                                    Modifier.fillMaxHeight()
                                } else Modifier
                            )
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