package com.example.fitnessway.feature.lists.screen.main

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.fitnessway.data.mappers.toPascalSpaced
import com.example.fitnessway.data.model.m_26.EdibleListFilter
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.feature.lists.screen.main.composables.PendingFoodsPagination
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
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
    val pendingFoodRepoUiState by viewModel.pendingFoodRepoUiState.collectAsState()
    val userFoodRepoUiState by viewModel.userFoodRepoUiState.collectAsState()
    val userSupplementUiState by viewModel.userSupplementRepoUiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()

    val user = userFlow
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState
    val userPendingFoodsUiStatePager = pendingFoodRepoUiState.uiStatePager
    val userFoodsUiStatePager = userFoodRepoUiState.uiStatePager
    val userSupplementsUiStatePager = userSupplementUiState.uiStatePager

    val moreOptionsState = Structure.rememberMoreOptionsState()
    val view = LocalView.current

    val dismissReviewErrorMessage = Ui.handleTempApiErrMsg(
        uiState = uiState.reviewDismissState,
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
            EdibleListFilter.SUPPLEMENT_REQUEST -> {}
        }
    }

    Screen(
        header = {
            Header(
                title = "My ${selectedList.name.toPascalSpaced()}s",
                extraContent = {
                    Clickables.AppPngIconButton(
                        icon = Structure.AppIconSource.Vector(Icons.Default.EditNote),
                        contentDescription = "Create a ${selectedList.name.toPascalSpaced()}",
                        enabled = nutrientsUiState is UiState.Success && when (selectedList) {
                            EdibleListFilter.FOOD -> userFoodsUiStatePager.uiState is UiState.Success
                            EdibleListFilter.FOOD_REQUEST -> userPendingFoodsUiStatePager.uiState is UiState.Success
                            EdibleListFilter.SUPPLEMENT -> userSupplementsUiStatePager.uiState is UiState.Success
                            else -> false
                        },
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            when (selectedList) {
                                EdibleListFilter.FOOD -> onNavigateToEdibleCreationForm()
                                EdibleListFilter.FOOD_REQUEST -> onNavigateToEdibleRequestScreen()
                                EdibleListFilter.SUPPLEMENT -> onNavigateToEdibleCreationForm()
                                EdibleListFilter.SUPPLEMENT_REQUEST -> {}
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
            Column {
                val isPendingFoodsVisible = selectedList == EdibleListFilter.FOOD_REQUEST

                Column(verticalArrangement = Arrangement.spacedBy(if (isPendingFoodsVisible) 16.dp else 0.dp)) {
                    Banners.ErrorBannerAnimated(
                        isVisible = isPendingFoodsVisible && dismissReviewErrorMessage != null,
                        text = dismissReviewErrorMessage ?: ""
                    )

                    PendingFoodsPagination(
                        uiStatePager = userPendingFoodsUiStatePager,
                        isVisible = isPendingFoodsVisible,
                        isUserPremium = user?.isPremium ?: false,
                        isDismissError = uiState.reviewDismissState is UiState.Error,
                        onLoadMore = viewModel::getMorePendingFoods,
                        onFoodClick = {
                            viewModel.requestManager.setPendingFood(it)
                            onNavigateToPendingEdibleDetails()
                        },
                        onDismissReview = { id ->
                            viewModel.resetReviewDismissState()
                            viewModel.requestManager.setReviewIdToRemove(id)
                            viewModel.dismissReview()
                        }
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
                    text = "${option.name.toPascalSpaced()}s",
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