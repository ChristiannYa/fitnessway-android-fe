package com.example.fitnessway.feature.home.screen.foodselection

import androidx.activity.compose.BackHandler
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
import com.example.fitnessway.data.mappers.toPascalSpaced
import com.example.fitnessway.data.model.m_26.FoodSource
import com.example.fitnessway.data.model.m_26.FoodToLogSearchCriteria
import com.example.fitnessway.feature.home.screen.foodselection.composables.AppFoodSearchBar
import com.example.fitnessway.feature.home.screen.foodselection.composables.FoodResultsPagination
import com.example.fitnessway.feature.home.screen.foodselection.composables.RecentlyLoggedFoods
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodSelectionScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToSelectedFood: () -> Unit,
    onPopBackStack: () -> Unit,
) {
    val appFoodRepoUiState by viewModel.appFoodRepoUiState.collectAsState()
    // val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()
    val foodLogRepoUiState by viewModel.foodLogRepoUiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val foodLogCategory by viewModel.foodLogCategory.collectAsState()
    val appFoodSearchQuery by viewModel.appFoodSearchQuery.collectAsState()

    val user = userFlow
    val appFoodsUiStatePager = appFoodRepoUiState.appFoodsUiStatePager
    val recentlyLoggedUiStatePager = foodLogRepoUiState.recentlyLogged
    // val foodsUiState = foodRepoUiState.foodsUiState

    fun onBackClick() {
        viewModel.onResetFoodSelectionScreen()
        onPopBackStack()
    }

    BackHandler { onBackClick() }

    LaunchedEffect(Unit) {
        viewModel.getRecentlyLoggedFoods()
    }

    if (user != null) {
        val foodLogCategoryCopy = foodLogCategory

        if (foodLogCategoryCopy != null) {
            val categoryString = foodLogCategoryCopy.name.toPascalSpaced()

            Screen(
                header = {
                    Header(
                        onBackClick = ::onBackClick,
                        title = "$categoryString selection",
                    )
                },
            ) { focusManager ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    var isTyping by remember { mutableStateOf(false) }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        AppFoodSearchBar(
                            query = appFoodSearchQuery,
                            onQueryChange = { q ->
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
                            onLoadMore = { viewModel.loadMoreAppFoods(appFoodSearchQuery) },
                            onFoodClick = {
                                viewModel.setSearchCriteria(FoodToLogSearchCriteria(it, FoodSource.APP))
                                onNavigateToSelectedFood()
                            }
                        )
                    }

                    RecentlyLoggedFoods(
                        modifier = Modifier
                            .then(
                                if (!appFoodSearchQuery.isNotBlank()) {
                                    Modifier.fillMaxHeight()
                                } else Modifier
                            ),
                        uiStatePager = recentlyLoggedUiStatePager,
                        isUserPremium = user.isPremium,
                        onLoadMore = viewModel::loadMoreRecentlyLoggedFoods,
                        onFoodClick = { }
                    )
                }
            }

        }
    }
}