package com.example.fitnessway.feature.lists.screen.main

import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MFood.Enum.ListOption
import com.example.fitnessway.feature.lists.screen.main.composables.SupplementList
import com.example.fitnessway.feature.lists.screen.main.composables.ToggleListViewButtons
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.util.UFood.Ui.foodsListWithState
import com.example.fitnessway.util.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onViewFoodDetails: () -> Unit,
    onNavigateToFoodCreationForm: () -> Unit
) {
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()
    val selectedList by viewModel.selectedList.collectAsState()

    val foodsUiState = foodRepoUiState.foodsUiState

    LaunchedEffect(Unit) {
        viewModel.getFoods()
    }

    val pullToRefreshState = rememberPullToRefreshState()
    val pullToRefreshThresholdReached = pullToRefreshState.distanceFraction >= 1f
    val isRefreshing = pullToRefreshThresholdReached && foodsUiState is UiState.Loading

    val view = LocalView.current

    Screen {
        Box(modifier = Modifier.fillMaxWidth()) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                state = pullToRefreshState,
                onRefresh = viewModel::refreshListsData,
                indicator = {
                    Loading.RefreshByPullIndicator(
                        isRefreshing = isRefreshing,
                        state = pullToRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    stickyHeader {
                        ToggleListViewButtons(
                            onToggleSelectedList = {
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                viewModel.setSelectedList(it)
                            },
                            selectedOption = selectedList
                        )
                    }

                    when (selectedList) {
                        ListOption.Food -> {
                            foodsListWithState(
                                state = foodsUiState,
                                onFoodClick = { food ->
                                    viewModel.setSelectedFood(food)
                                    onViewFoodDetails()
                                },
                                loadingVerticalSpace = 16.dp
                            )
                        }

                        ListOption.Supplement -> {
                            item {
                                SupplementList()
                            }
                        }
                    }
                }
            }

            if (foodsUiState is UiState.Success) {
                IconButton(
                    onClick = onNavigateToFoodCreationForm,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(46.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.BottomCenter)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create",
                        tint = WhiteFont,
                        modifier = Modifier.fillMaxSize(0.8f)
                    )
                }
            }
        }
    }
}
