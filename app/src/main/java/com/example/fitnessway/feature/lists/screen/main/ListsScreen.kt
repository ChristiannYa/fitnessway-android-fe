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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.fitnessway.R
import com.example.fitnessway.data.model.MFood.Enum.ListOption
import com.example.fitnessway.feature.lists.screen.main.composables.SupplementList
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.ScreenOverlay
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.util.Formatters.snakeToReadableText
import com.example.fitnessway.util.UFood.Ui.foodsListWithState
import com.example.fitnessway.util.isLoading
import com.example.fitnessway.util.isSuccess
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onViewFoodDetails: () -> Unit,
    onNavigateToFoodCreationForm: () -> Unit
) {
    val userFlow by viewModel.userFlow.collectAsState()
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()

    val user = userFlow
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState
    val foodsUiState = foodRepoUiState.foodsUiState
    val areListsDataReady = nutrientsUiState.isSuccess && foodsUiState.isSuccess

    var selectedList by remember { mutableStateOf(ListOption.Food) }

    LaunchedEffect(Unit) {
        viewModel.getFoods()
        viewModel.getNutrients()
    }

    val moreOptionsState = Structure.rememberMoreOptionsState()

    val pullToRefreshState = rememberPullToRefreshState()
    val pullToRefreshThresholdReached = pullToRefreshState.distanceFraction >= 1f
    val isRefreshing = pullToRefreshThresholdReached && foodsUiState.isLoading

    val view = LocalView.current

    val title = when (selectedList) {
        ListOption.Food -> "Foods"
        ListOption.Supplement -> "Supplements"
    }

    Screen(
        header = {
            Header(
                title = title,
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

            val options = enumValues<ListOption>().map { option ->
                val isSelected = selectedList == option
                val tint = if (isSelected) WhiteFont else null

                Structure.MoreOptionsConfig(
                    text = option.name.lowercase().snakeToReadableText(),
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
                    onClick = onNavigateToFoodCreationForm,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(52.dp)
                        .background(MaterialTheme.colorScheme.inverseSurface)
                        .align(Alignment.BottomCenter)
                ) {
                    val imageId = when (selectedList) {
                        ListOption.Food -> R.drawable.food
                        ListOption.Supplement -> R.drawable.energy
                    }

                    Icon(
                        painter = painterResource(imageId),
                        contentDescription = "Create",
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