package com.example.fitnessway.feature.lists.screen.main

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.zIndex
import com.example.fitnessway.data.model.m_26.ListOption
import com.example.fitnessway.feature.lists.screen.main.composables.PendingFoodsPagination
import com.example.fitnessway.feature.lists.screen.main.composables.UserFoodsList
import com.example.fitnessway.feature.lists.screen.main.composables.UserSupplementsList
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.ScreenOverlay
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.plural
import com.example.fitnessway.util.splitPascalCase
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onViewFoodDetails: () -> Unit,
    onNavigateToFoodRequestScreen: () -> Unit,
    onNavigateToFoodCreationForm: () -> Unit
) {
    val selectedList by viewModel.selectedList.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val pendingFoodRepoUiState by viewModel.pendingFoodRepoUiState.collectAsState()
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()

    val user = userFlow
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState
    val pendingFoodsUiStatePager = pendingFoodRepoUiState.pendingFoodsUiStatePager
    val foodsUiState = foodRepoUiState.foodsUiState

    val moreOptionsState = Structure.rememberMoreOptionsState()
    val view = LocalView.current

    LaunchedEffect(Unit) {
        viewModel.getNutrients()
    }

    LaunchedEffect(selectedList) {
        when (selectedList) {
            ListOption.PendingFood -> viewModel.getPendingFoods()
            ListOption.Food -> viewModel.getFoods()
            ListOption.Supplement -> {}
        }
    }

    Screen(
        header = {
            Header(
                title = "My ${selectedList.name.splitPascalCase().plural()}",
                extraContent = {
                    Clickables.AppPngIconButton(
                        icon = selectedList.icon,
                        contentDescription = "Create ${selectedList.name.splitPascalCase()}",
                        enabled = nutrientsUiState is UiState.Success && when (selectedList) {
                            ListOption.PendingFood -> pendingFoodsUiStatePager.uiState is UiState.Success
                            ListOption.Food -> foodsUiState is UiState.Success
                            ListOption.Supplement -> false
                        },
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            when (selectedList) {
                                ListOption.PendingFood -> onNavigateToFoodRequestScreen()
                                ListOption.Food -> onNavigateToFoodCreationForm()
                                ListOption.Supplement -> {}
                            }
                        }
                    )

                    Clickables.AppPngIconButton(
                        icon = Structure.AppIconButtonSource.Vector(Icons.Default.Menu),
                        contentDescription = "View list options",
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            moreOptionsState.toggle()
                        }
                    )
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            PendingFoodsPagination(
                isVisible = selectedList == ListOption.PendingFood,
                isUserPremium = user?.isPremium ?: false,
                pendingFoodsUiStatePager = pendingFoodsUiStatePager,
                onLoadMore = viewModel::loadMorePendingFoods,
                onFoodClick = { pendingFood -> },
            )

            UserFoodsList(
                isVisible = selectedList == ListOption.Food,
                isUserPremium = user?.isPremium ?: false,
                foodsUiState = foodsUiState,
                onRefresh = viewModel::refreshFoods,
                onFoodClick = { food ->
                    viewModel.setSelectedFood(food)
                    onViewFoodDetails()
                }
            )

            UserSupplementsList(
                isVisible = selectedList == ListOption.Supplement
            )

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
                        viewModel.setSelectedList(option)
                    }
                )
            }.toTypedArray()

            Structure.MoreOptions(
                state = moreOptionsState,
                options = options,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .zIndex(1f)
            )

            ScreenOverlay.DarkOverlay(
                isVisible = moreOptionsState.isVisible,
                onClick = moreOptionsState::hide
            )
        }
    }
}