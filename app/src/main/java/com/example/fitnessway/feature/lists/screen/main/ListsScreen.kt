package com.example.fitnessway.feature.lists.screen.main

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.fitnessway.data.mappers.toEdibleListFilterMatched
import com.example.fitnessway.data.mappers.toEdibleType
import com.example.fitnessway.data.mappers.toTitleCase
import com.example.fitnessway.data.model.m_26.EdibleListFilter
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.feature.lists.screen.main.composables.PendingEdiblesPagination
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.edible.EdibleListSelectionTextButton
import com.example.fitnessway.ui.edible.FoodPreviewList
import com.example.fitnessway.ui.shared.Banners
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.ScreenOverlay
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.extensions.getAppIconSource
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onNavigateToUserEdibleDetails: () -> Unit,
    onNavigateToPendingEdibleDetails: () -> Unit,
    onNavigateToEdibleRequestScreen: () -> Unit,
    onNavigateToEdibleCreationForm: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedList by viewModel.edibleListFilter.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val userFoodRepoUiState by viewModel.userFoodRepoUiState.collectAsState()
    val userSupplementRepoUiState by viewModel.userSupplementRepoUiState.collectAsState()
    val pendingFoodRepoUiState by viewModel.pendingFoodRepoUiState.collectAsState()
    val pendingSupplementRepoUiState by viewModel.pendingSupplementRepoUiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()

    val user = userFlow
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState
    val userFoodsUiStatePager = userFoodRepoUiState.uiStatePager
    val userSupplementsUiStatePager = userSupplementRepoUiState.uiStatePager
    val pendingFoodsUiStatePager = pendingFoodRepoUiState.uiStatePager
    val pendingSupplementsUiStatePager = pendingSupplementRepoUiState.uiStatePager

    val moreOptionsState = Structure.rememberMoreOptionsState()
    val view = LocalView.current

    val foodReviewDismissErrorMessage = Ui.handleTempApiErrMsg(
        uiState = uiState.foodReviewDismissState,
        onTimeOut = viewModel::resetReviewDismissState
    )

    LaunchedEffect(Unit) {
        viewModel.getNutrients()
    }

    LaunchedEffect(selectedList) {
        when (selectedList) {
            EdibleListFilter.FOOD -> viewModel.getFoods()
            EdibleListFilter.FOOD_REQUEST -> viewModel.getPendingFoods()
            EdibleListFilter.SUPPLEMENT -> viewModel.getSupplements()
            EdibleListFilter.SUPPLEMENT_REQUEST -> viewModel.getPendingSupplements()
        }
    }

    Screen(
        header = {
            Header(
                title = "My ${selectedList.name.toTitleCase()}s",
                extraContent = {
                    Clickables.AppPngIconButton(
                        icon = Structure.AppIconSource.Vector(Icons.Default.EditNote),
                        contentDescription = "Create a ${selectedList.name.toTitleCase()}",
                        enabled = nutrientsUiState is UiState.Success && when (selectedList) {
                            EdibleListFilter.FOOD -> userFoodsUiStatePager.uiState is UiState.Success
                            EdibleListFilter.FOOD_REQUEST -> pendingFoodsUiStatePager.uiState is UiState.Success
                            EdibleListFilter.SUPPLEMENT -> userSupplementsUiStatePager.uiState is UiState.Success
                            EdibleListFilter.SUPPLEMENT_REQUEST -> pendingSupplementsUiStatePager.uiState is UiState.Success
                        },
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            when (selectedList) {
                                EdibleListFilter.FOOD -> onNavigateToEdibleCreationForm()
                                EdibleListFilter.FOOD_REQUEST -> onNavigateToEdibleRequestScreen()
                                EdibleListFilter.SUPPLEMENT -> onNavigateToEdibleCreationForm()
                                EdibleListFilter.SUPPLEMENT_REQUEST -> onNavigateToEdibleRequestScreen()
                            }
                        }
                    )

                    Clickables.AppPngIconButton(
                        icon = Structure.AppIconSource.Vector(Icons.Default.Menu),
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val isPendingFoodsVisible = selectedList == EdibleListFilter.FOOD_REQUEST
                val isPendingSupplementsVisible = selectedList == EdibleListFilter.SUPPLEMENT_REQUEST

                val isListSelected = when (selectedList.toEdibleType()) {
                    EdibleType.FOOD -> !isPendingFoodsVisible
                    EdibleType.SUPPLEMENT -> !isPendingSupplementsVisible
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    EdibleListSelectionTextButton(
                        text = "List",
                        isSelected = isListSelected,
                        onClick = {
                            when (selectedList.toEdibleType()) {
                                EdibleType.FOOD -> if (isPendingFoodsVisible)
                                    viewModel.setListType(EdibleListFilter.FOOD)

                                EdibleType.SUPPLEMENT -> if (isPendingSupplementsVisible)
                                    viewModel.setListType(EdibleListFilter.SUPPLEMENT)
                            }
                        }
                    )

                    EdibleListSelectionTextButton(
                        text = "Requests",
                        isSelected = !isListSelected,
                        onClick = {
                            when (selectedList.toEdibleType()) {
                                EdibleType.FOOD -> if (!isPendingFoodsVisible)
                                    viewModel.setListType(EdibleListFilter.FOOD_REQUEST)

                                EdibleType.SUPPLEMENT -> if (!isPendingSupplementsVisible)
                                    viewModel.setListType(EdibleListFilter.SUPPLEMENT_REQUEST)
                            }
                        }
                    )
                }

                fun handlePendingEdibleClick(edible: PendingFood) {
                    viewModel.requestManager.setPendingFood(edible)
                    onNavigateToPendingEdibleDetails()
                }

                fun handlePendingEdibleDismiss(id: Int) {
                    viewModel.resetReviewDismissState()
                    viewModel.requestManager.setReviewIdToRemove(id)
                    viewModel.dismissReview()
                }

                Column(verticalArrangement = Arrangement.spacedBy(if (isPendingFoodsVisible) 16.dp else 0.dp)) {
                    Banners.ErrorBannerAnimated(
                        isVisible = isPendingFoodsVisible && foodReviewDismissErrorMessage != null,
                        text = foodReviewDismissErrorMessage ?: ""
                    )

                    PendingEdiblesPagination(
                        uiStatePager = pendingFoodsUiStatePager,
                        isVisible = isPendingFoodsVisible,
                        isUserPremium = user?.isPremium ?: false,
                        isDismissError = uiState.foodReviewDismissState is UiState.Error,
                        onLoadMore = viewModel::getMorePendingFoods,
                        onEdibleClick = ::handlePendingEdibleClick,
                        onDismissReview = ::handlePendingEdibleDismiss
                    )
                }

                Column() {
                    PendingEdiblesPagination(
                        uiStatePager = pendingSupplementsUiStatePager,
                        isVisible = isPendingSupplementsVisible,
                        isUserPremium = user?.isPremium ?: false,
                        isDismissError = false,
                        onLoadMore = viewModel::getMorePendingSupplements,
                        onEdibleClick = ::handlePendingEdibleClick,
                        onDismissReview = ::handlePendingEdibleDismiss
                    )
                }

                FoodPreviewList(
                    uiStatePager = userFoodsUiStatePager,
                    isVisible = selectedList == EdibleListFilter.FOOD,
                    isUserPremium = user?.isPremium ?: false,
                    onLoadMore = viewModel::getMoreFoods,
                    onFoodClick = { food ->
                        viewModel.editionManager.setSelectedEdible(food)
                        onNavigateToUserEdibleDetails()
                    }
                )

                FoodPreviewList(
                    uiStatePager = userSupplementsUiStatePager,
                    isVisible = selectedList == EdibleListFilter.SUPPLEMENT,
                    isUserPremium = user?.isPremium ?: false,
                    onLoadMore = viewModel::getMoreSupplements,
                    onFoodClick = { food ->
                        viewModel.editionManager.setSelectedEdible(food)
                        onNavigateToUserEdibleDetails()
                    }
                )
            }


            val options = enumValues<EdibleType>().map { option ->
                val isSelected = selectedList.toEdibleType() == option

                Structure.MoreOptionsConfig(
                    text = "${option.name.toTitleCase()}s",
                    backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else null,
                    icon = option.getAppIconSource(),
                    iconTint = if (isSelected) WhiteFont else null,
                    onClick = {
                        moreOptionsState.hide()
                        viewModel.setListType(option.toEdibleListFilterMatched())
                    }
                )
            }.toTypedArray()

            // @TODO: Fix pop-out animation animation stutter when fetching for pending
            //        edibles for the first time
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