package com.example.fitnessway.feature.home.screen.foodselection

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toPascalSpaced
import com.example.fitnessway.data.model.m_26.FoodLogListFilter
import com.example.fitnessway.data.model.m_26.FoodSource
import com.example.fitnessway.data.model.m_26.FoodToLogSearchCriteria
import com.example.fitnessway.feature.home.screen.foodselection.composables.AppFoodResultsPagination
import com.example.fitnessway.feature.home.screen.foodselection.composables.AppFoodSearchBar
import com.example.fitnessway.feature.home.screen.foodselection.composables.RecentlyLoggedFoods
import com.example.fitnessway.feature.home.screen.foodselection.composables.UserFoodsList
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.AppModifiers
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.UiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodSelectionScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToSelectedFood: () -> Unit,
    onPopBackStack: () -> Unit,
) {
    val user by viewModel.userFlow.collectAsState()

    val appFoodRepoUiState by viewModel.appFoodRepoUiState.collectAsState()
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()
    val foodLogRepoUiState by viewModel.foodLogRepoUiState.collectAsState()

    val foodList by viewModel.foodList.collectAsState()
    val appFoodSearchQuery by viewModel.appFoodSearchQuery.collectAsState()

    val foodLogCategory = viewModel.foodLogCategory.collectAsState().value

    val appFoodsUiStatePager = appFoodRepoUiState.appFoodsUiStatePager
    val recentlyLoggedUiStatePager = foodLogRepoUiState.recentlyLogged
    val foodsUiState = foodRepoUiState.foodsUiState

    fun onBackClick() {
        viewModel.onResetFoodSelectionScreen()
        onPopBackStack()
    }

    BackHandler { onBackClick() }

    LaunchedEffect(foodList) {
        when (foodList) {
            FoodLogListFilter.RECENTLY_LOGGED -> viewModel.getRecentlyLoggedFoods()
            FoodLogListFilter.USER_FOODS -> viewModel.getFoods()
        }
    }

    if (foodLogCategory != null) {
        val categoryString = foodLogCategory.name.toPascalSpaced()

        Screen(
            header = {
                Header(
                    onBackClick = ::onBackClick,
                    title = "$categoryString selection",
                )
            },
        ) { focusManager ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Column {
                    var isTyping by remember { mutableStateOf(false) }
                    val isLoading = isTyping || appFoodsUiStatePager.uiState is UiState.Loading

                    AppFoodSearchBar(
                        query = appFoodSearchQuery,
                        onQueryChange = { q ->
                            isTyping = q.isNotBlank()
                            viewModel.getAppFoods(q)
                        },
                        focusManager = focusManager
                    )

                    AppFoodResultsPagination(
                        isLoading = isLoading,
                        isUserPremium = user?.isPremium ?: false,
                        appFoodsUiStatePager = appFoodsUiStatePager,
                        onTypingConsumed = { isTyping = false },
                        onLoadMore = { viewModel.getMoreAppFoods(appFoodSearchQuery) },
                        onFoodClick = {
                            viewModel.setSearchCriteria(FoodToLogSearchCriteria(it, FoodSource.APP))
                            onNavigateToSelectedFood()
                        },
                        modifier = Modifier
                            .animateContentSize()
                            .then(
                                if (isTyping || appFoodsUiStatePager.uiState !is UiState.Idle) {
                                    Modifier.fillMaxHeight()
                                } else Modifier
                            )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    FilterButton("Recently Logged", foodList == FoodLogListFilter.RECENTLY_LOGGED, {
                        viewModel.setFoodList(FoodLogListFilter.RECENTLY_LOGGED)
                    })

                    FilterButton("My Foods", foodList == FoodLogListFilter.USER_FOODS, {
                        viewModel.setFoodList(FoodLogListFilter.USER_FOODS)
                    })
                }

                RecentlyLoggedFoods(
                    uiStatePager = recentlyLoggedUiStatePager,
                    isVisible = foodList == FoodLogListFilter.RECENTLY_LOGGED,
                    isUserPremium = user?.isPremium ?: false,
                    onLoadMore = viewModel::getMoreRecentlyLoggedFoods,
                    onFoodClick = { id, source ->
                        viewModel.setSearchCriteria(FoodToLogSearchCriteria(id, source))
                        onNavigateToSelectedFood()
                    }
                )

                UserFoodsList(
                    foodsUiState = foodsUiState,
                    isVisible = foodList == FoodLogListFilter.USER_FOODS,
                    isUserPremium = user?.isPremium ?: false,
                    onFoodClick = {
                        viewModel.setSearchCriteria(FoodToLogSearchCriteria(it.information.id, FoodSource.USER))
                        onNavigateToSelectedFood()
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .areaContainer(
                size = AppModifiers.AreaContainerSize.EXTRA_SMALL,
                areaColor = Color.Transparent,
                hugsContent = true,
                onClick = onClick
            )
    ) {
        val color = MaterialTheme.colorScheme.onBackground

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = if (isSelected) color else color.copy(0.5f)
        )
    }
}