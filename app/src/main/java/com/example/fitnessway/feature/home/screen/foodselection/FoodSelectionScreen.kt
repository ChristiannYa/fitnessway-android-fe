package com.example.fitnessway.feature.home.screen.foodselection

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
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
import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.FoodLogCategory
import com.example.fitnessway.data.model.m_26.FoodLogListFilter
import com.example.fitnessway.data.model.m_26.FoodToLogSearchCriteria
import com.example.fitnessway.feature.home.screen.foodselection.composables.AppFoodResultsPagination
import com.example.fitnessway.feature.home.screen.foodselection.composables.AppFoodSearchBar
import com.example.fitnessway.feature.home.screen.foodselection.composables.RecentlyLoggedFoods
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.edible.EdibleListSelectionTextButton
import com.example.fitnessway.ui.edible.FoodPreviewList
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
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
    val userFoodRepoUiState by viewModel.userFoodRepoUiState.collectAsState()
    val userSupplementRepoUiState by viewModel.userSupplementRepoUiState.collectAsState()
    val foodRecentLogRepoUiState by viewModel.foodRecentLogRepoUiState.collectAsState()
    val supplementRecentLogRepoUiState by viewModel.supplementRecentLogRepoUiState.collectAsState()

    val foodList by viewModel.foodList.collectAsState()
    val appFoodSearchQuery by viewModel.appFoodSearchQuery.collectAsState()

    val foodLogCategory = viewModel.foodLogCategory.collectAsState().value

    val appFoodsUiStatePager = appFoodRepoUiState.appFoodsUiStatePager
    val foodRecentLogRepoUiStatePager = foodRecentLogRepoUiState.uiStatePager
    val supplementRecentLogRepoUiStatePager = supplementRecentLogRepoUiState.uiStatePager
    val userFoodsUiStatePager = userFoodRepoUiState.uiStatePager
    val userSupplementsUiStatePager = userSupplementRepoUiState.uiStatePager

    fun onBackClick() {
        viewModel.onResetFoodSelectionScreen()
        onPopBackStack()
    }

    BackHandler { onBackClick() }

    LaunchedEffect(foodList) {
        foodList?.let {
            when (it) {
                FoodLogListFilter.RECENTLY_LOGGED_FOODS -> {
                    if (foodLogCategory != FoodLogCategory.SUPPLEMENT) viewModel.getRecentlyLoggedFoods()
                }

                FoodLogListFilter.RECENTLY_LOGGED_SUPPLEMENTS -> {
                    if (foodLogCategory == FoodLogCategory.SUPPLEMENT) viewModel.getRecentlyLoggedSupplements()
                }

                FoodLogListFilter.USER_FOODS -> viewModel.getUserFoods()

                FoodLogListFilter.USER_SUPPLEMENTS -> viewModel.getUserSupplements()
            }
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
                            viewModel.setSearchCriteria(
                                FoodToLogSearchCriteria(
                                    id = it,
                                    source = EdibleSource.APP,
                                    edibleType = EdibleType.FOOD
                                )
                            )
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

                val isRecentlyLoggedFoodsVisible = foodList == FoodLogListFilter.RECENTLY_LOGGED_FOODS &&
                        foodLogCategory != FoodLogCategory.SUPPLEMENT

                val isRecentlyLoggedSupplementsVisible = foodList == FoodLogListFilter.RECENTLY_LOGGED_SUPPLEMENTS &&
                        foodLogCategory == FoodLogCategory.SUPPLEMENT

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    EdibleListSelectionTextButton(
                        text = "Recently Logged",
                        isSelected = when (foodLogCategory) {
                            FoodLogCategory.SUPPLEMENT -> isRecentlyLoggedSupplementsVisible
                            else -> isRecentlyLoggedFoodsVisible
                        },
                        onClick = {
                            when (foodLogCategory) {
                                FoodLogCategory.SUPPLEMENT -> viewModel.setFoodList(FoodLogListFilter.RECENTLY_LOGGED_SUPPLEMENTS)
                                else -> viewModel.setFoodList(FoodLogListFilter.RECENTLY_LOGGED_FOODS)
                            }
                        }
                    )

                    when (foodLogCategory) {
                        FoodLogCategory.SUPPLEMENT -> EdibleListSelectionTextButton(
                            text = "My Supplements",
                            isSelected = foodList == FoodLogListFilter.USER_SUPPLEMENTS,
                            onClick = { viewModel.setFoodList(FoodLogListFilter.USER_SUPPLEMENTS) }
                        )

                        else -> EdibleListSelectionTextButton(
                            text = "My Foods",
                            isSelected = foodList == FoodLogListFilter.USER_FOODS,
                            onClick = { viewModel.setFoodList(FoodLogListFilter.USER_FOODS) }
                        )
                    }
                }

                RecentlyLoggedFoods(
                    uiStatePager = foodRecentLogRepoUiStatePager,
                    edibleType = EdibleType.FOOD,
                    isVisible = isRecentlyLoggedFoodsVisible,
                    isUserPremium = user?.isPremium ?: false,
                    onLoadMore = viewModel::getMoreRecentlyLoggedFoods,
                    onFoodClick = { id, source ->
                        viewModel.setSearchCriteria(
                            FoodToLogSearchCriteria(
                                id = id,
                                source = source,
                                edibleType = EdibleType.FOOD
                            )
                        )
                        onNavigateToSelectedFood()
                    }
                )

                RecentlyLoggedFoods(
                    uiStatePager = supplementRecentLogRepoUiStatePager,
                    edibleType = EdibleType.SUPPLEMENT,
                    isVisible = isRecentlyLoggedSupplementsVisible,
                    isUserPremium = user?.isPremium ?: false,
                    onLoadMore = viewModel::getMoreRecentlyLoggedSupplements,
                    onFoodClick = { id, source ->
                        viewModel.setSearchCriteria(
                            FoodToLogSearchCriteria(
                                id = id,
                                source = source,
                                edibleType = EdibleType.SUPPLEMENT
                            )
                        )
                    }
                )

                FoodPreviewList(
                    uiStatePager = userFoodsUiStatePager,
                    isVisible = foodList == FoodLogListFilter.USER_FOODS &&
                            foodLogCategory != FoodLogCategory.SUPPLEMENT,
                    isUserPremium = user?.isPremium ?: false,
                    onLoadMore = viewModel::getMoreUserFoods,
                    onFoodClick = { food ->
                        viewModel.setSearchCriteria(
                            FoodToLogSearchCriteria(
                                id = food.id,
                                source = EdibleSource.USER,
                                edibleType = EdibleType.FOOD
                            )
                        )
                        onNavigateToSelectedFood()
                    }
                )

                FoodPreviewList(
                    uiStatePager = userSupplementsUiStatePager,
                    isVisible = foodList == FoodLogListFilter.USER_SUPPLEMENTS &&
                            foodLogCategory == FoodLogCategory.SUPPLEMENT,
                    isUserPremium = user?.isPremium ?: false,
                    onLoadMore = viewModel::getMoreUserSupplements,
                    onFoodClick = { food ->
                        viewModel.setSearchCriteria(
                            FoodToLogSearchCriteria(
                                id = food.id,
                                source = EdibleSource.USER,
                                edibleType = EdibleType.SUPPLEMENT
                            )
                        )
                        onNavigateToSelectedFood()
                    }
                )
            }
        }
    }
}