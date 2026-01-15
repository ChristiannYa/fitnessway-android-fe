package com.example.fitnessway.feature.home.screen.foodselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MFood.Enum.FoodSort
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.DarkOverlay
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.ScreenOverlay
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.shared.Structure.AppIconButtonSource
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.util.Formatters.formatUiErrorMessage
import com.example.fitnessway.util.Formatters.snakeToReadableText
import com.example.fitnessway.util.UFood.Ui.getFoodLogCategory
import com.example.fitnessway.util.UFood.foodsListWithState
import com.example.fitnessway.util.Ui.handleTempApiErrorMessage
import com.example.fitnessway.util.UiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodSelectionScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onNavigateToSelectedFood: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()
    val selectedFoodSort by viewModel.selectedFoodSort.collectAsState()
    val foodLogCategory by viewModel.foodLogCategory.collectAsState()

    val user = viewModel.user
    val foodsUiState = foodRepoUiState.foodsUiState
    val foodSortUiState = foodRepoUiState.foodSortUiState
    val foodSortUpdateState = uiState.foodSortUpdateState

    LaunchedEffect(Unit) {
        viewModel.getFoods()

        if (user?.isPremium == true) {
            viewModel.getFoodSort()
        }
    }

    LaunchedEffect(foodSortUiState) {
        if (foodSortUiState is UiState.Success) {
            viewModel.setSelectedFoodSort(foodSort = foodSortUiState.data)
        }
    }

    val foodSortErrorMessage = handleTempApiErrorMessage(
        uiState = foodSortUpdateState,
        onTimeOut = viewModel::resetFoodSortUpdateState
    )

    if (user != null) {
        val selectedFoodSortCopy = selectedFoodSort
        val foodLogCategoryCopy = foodLogCategory

        if (foodLogCategoryCopy != null) {
            val moreOptionsState = Structure.rememberMoreOptionsState()
            val categoryString = getFoodLogCategory(foodLogCategoryCopy)

            Screen(
                header = {
                    Header(
                        onBackClick = onBackClick,
                        title = "$categoryString selection",
                        isOnBackEnabled = foodSortUpdateState !is UiState.Loading
                    ) {
                        if (foodSortUiState is UiState.Success) {
                            Clickables.AppPngIconButton(
                                onClick = moreOptionsState::toggle,
                                enabled = foodSortUpdateState !is UiState.Loading,
                                contentDescription = "Filter sort display",
                                icon = AppIconButtonSource.Vector(Icons.Default.FilterList)
                            )
                        }
                    }
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ErrorBannerAnimated(
                            isVisible = foodSortErrorMessage != null,
                            text = foodSortErrorMessage ?: ""
                        )

                        Box {
                            Foods(
                                state = foodsUiState,
                                onFoodClick = { food ->
                                    viewModel.setSelectedFoodToLog(food)
                                    onNavigateToSelectedFood()
                                },
                            )

                            ScreenOverlay.Loading(
                                isVisible = foodSortUpdateState is UiState.Loading ||
                                        foodsUiState is UiState.Loading
                            )
                        }
                    }

                    if (foodSortUiState is UiState.Success) {
                        DarkOverlay(
                            isVisible = moreOptionsState.isVisible,
                            onClick = moreOptionsState::hide
                        )

                        val options = enumValues<FoodSort>().map { sortType ->
                            val isSelected = sortType.name.equals(selectedFoodSortCopy, true)
                            val tint = if (isSelected) WhiteFont else null

                            Structure.MoreOptionsConfig(
                                text = sortType.name.lowercase().snakeToReadableText(),
                                backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else null,
                                icon = sortType.icon,
                                iconTint = tint,
                                iconModifier = Modifier.size(16.dp),
                                textColor = tint,
                                onClick = {
                                    moreOptionsState.hide()
                                    viewModel.updateFoodSort(foodSort = sortType.name.lowercase())
                                }
                            )
                        }.toTypedArray()

                        Structure.MoreOptions(
                            state = moreOptionsState,
                            options = options,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
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

@Composable
private fun Foods(
    state: UiState<List<FoodInformation>>,
    onFoodClick: (FoodInformation) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxHeight()
    ) {
        foodsListWithState(
            state = state,
            showsNutrientPreview = true,
            onFoodClick = onFoodClick
        )
    }

    NotFoundMessageAnimated(
        isVisible = state is UiState.Error,
        message = formatUiErrorMessage(state)
    )
}