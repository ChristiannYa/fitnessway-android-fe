package com.example.fitnessway.feature.lists.screen.main

import android.view.SoundEffectConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.fitnessway.data.model.m_26.ListOption
import com.example.fitnessway.feature.lists.screen.main.composables.SupplementList
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.ScreenOverlay
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.UFood.Ui.foodsListWithState
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.pagination
import com.example.fitnessway.util.plural
import com.example.fitnessway.util.splitPascalCase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onViewFoodDetails: () -> Unit,
    onNavigateToFoodRequestScreen: () -> Unit,
    onNavigateToFoodCreationForm: () -> Unit
) {
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()

    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState
    val pendingFoodsUiStatePager = foodRepoUiState.pendingFoodsUiStatePager
    val foodsUiState = foodRepoUiState.foodsUiState

    val moreOptionsState = Structure.rememberMoreOptionsState()
    var selectedList by remember { mutableStateOf(ListOption.Food) }
    val pendingFoodsListState = rememberLazyListState()

    val pullToRefreshState = rememberPullToRefreshState()
    val pullToRefreshThresholdReached = pullToRefreshState.distanceFraction >= 1f
    val isRefreshing = pullToRefreshThresholdReached && foodsUiState is UiState.Loading

    val areListsDataReady = nutrientsUiState is UiState.Success &&
            foodsUiState is UiState.Success

    val view = LocalView.current

    LaunchedEffect(Unit) {
        viewModel.getNutrients()
    }

    LaunchedEffect(selectedList) {
        when (selectedList) {
            ListOption.PendingFood -> {
                viewModel.getPendingFoods()
            }

            ListOption.Food -> {
                viewModel.getFoods()
            }

            else -> {}
        }
    }

    LaunchedEffect(pendingFoodsListState) {
        snapshotFlow {
            val layoutInfo = pendingFoodsListState.layoutInfo
            val viewportEndOffset = layoutInfo.viewportEndOffset

            val totalItemsCount = layoutInfo.totalItemsCount
            val lastItem = layoutInfo.visibleItemsInfo.lastOrNull()

            if (lastItem != null) logcat("last item: ${lastItem.index}")

            lastItem?.let {
                val isLastItem = lastItem.index == totalItemsCount - 1

                val lastItemYBottomCord = lastItem.offset + lastItem.size
                val isFullyVisible = lastItemYBottomCord <= viewportEndOffset

                totalItemsCount > 0 && isLastItem && isFullyVisible
            } ?: false
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { viewModel.loadMorePendingFoods() }
    }

    Screen(
        header = {
            Header(
                title = "My ${selectedList.name.splitPascalCase().plural()}",
                extraContent = if (areListsDataReady) {
                    {
                        Clickables.AppPngIconButton(
                            icon = Structure.AppIconButtonSource.Vector(Icons.Default.Menu),
                            contentDescription = "View list options",
                            onClick = {
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                moreOptionsState.toggle()
                            }
                        )
                    }
                } else null
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AnimatedVisibility(
                visible = selectedList == ListOption.PendingFood
            ) {
                Box(modifier = Modifier.fillMaxHeight()) {
                    LazyColumn(
                        state = pendingFoodsListState,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        itemsIndexed(
                            pendingFoodsUiStatePager.pagination?.data ?: emptyList(),
                        ) { index, food ->
                            val foodBase = food.information.base

                            Box(modifier = Modifier.areaContainer()) {
                                Column {
                                    Text("$index ${foodBase.name}")
                                    Text(foodBase.brand ?: "~")
                                    Text(food.status.name.splitPascalCase())
                                }
                            }
                        }

                        if (pendingFoodsUiStatePager.pagination?.hasMorePages == true) {
                            item {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(
                                                Ui.Measurements.LOADING_CIRCLE_IN_HEADER_SIZE
                                            ),
                                        strokeWidth = Ui.Measurements.LOADING_CIRCLE_IN_HEADER_STROKE_WIDTH
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = selectedList == ListOption.Food,
            ) {
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
                        foodsListWithState(
                            state = foodsUiState,
                            onFoodClick = { food ->
                                viewModel.setSelectedFood(food)
                                onViewFoodDetails()
                            },
                            loadingVerticalSpace = 16.dp
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = selectedList == ListOption.Supplement
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    item {
                        SupplementList()
                    }
                }
            }

            val options = enumValues<ListOption>().map { option ->
                val isSelected = selectedList == option
                val tint = if (isSelected) WhiteFont else null

                Structure.MoreOptionsConfig(
                    text = option.name.splitPascalCase().plural(),
                    backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else null,
                    icon = option.icon,
                    iconTint = tint,
                    onClick = {
                        moreOptionsState.hide()
                        selectedList = option
                    }
                )
            }.toTypedArray()

            if (areListsDataReady) {
                Structure.MoreOptions(
                    state = moreOptionsState,
                    options = options,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .zIndex(1f)
                )

                IconButton(
                    onClick = {
                        when (selectedList) {
                            ListOption.PendingFood -> onNavigateToFoodRequestScreen()
                            ListOption.Food -> onNavigateToFoodCreationForm()
                            else -> {}
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(52.dp)
                        .background(MaterialTheme.colorScheme.inverseSurface)
                        .align(Alignment.BottomCenter)
                ) {
                    Structure.AppIconDynamic(
                        source = selectedList.icon,
                        contentDescription = "Create ${selectedList.name.splitPascalCase()}",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxSize(0.7f)
                    )
                }

                ScreenOverlay.DarkOverlay(
                    isVisible = moreOptionsState.isVisible,
                    onClick = moreOptionsState::hide
                )
            }
        }
    }
}