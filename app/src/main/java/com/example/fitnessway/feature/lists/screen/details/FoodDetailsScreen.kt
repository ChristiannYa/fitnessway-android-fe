package com.example.fitnessway.feature.lists.screen.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.lists.screen.details.composables.ConfirmFoodDeletionPopup
import com.example.fitnessway.feature.lists.screen.details.composables.FoodInformation
import com.example.fitnessway.feature.lists.screen.details.composables.FoodMoreOptionsButton
import com.example.fitnessway.feature.lists.screen.details.composables.MoreOptionsPopup
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.DarkOverlay
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Messages.SuccessMessageAnimated
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Ui.handleTempApiErrorMessage
import com.example.fitnessway.util.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodDetailsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onNavigateToEditionScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFood by viewModel.selectedFood.collectAsState()

    val user = viewModel.user
    val food = selectedFood
    val foodDeleteState = uiState.foodDeleteState
    val title = "Food Details"

    val foodDeleteErrorMessage = handleTempApiErrorMessage(
        uiState = foodDeleteState,
        onTimeOut = viewModel::resetFoodDeleteState
    )

    if (food == null || user == null) {
        Screen(
            header = { Header(onBackClick = onBackClick, title = title) },
            content = { NotFoundMessage("Food data not found") }
        )
    } else {
        var isMoreOptionsPopupVisible by remember { mutableStateOf(false) }
        var isConfirmDeletionPopupVisible by remember { mutableStateOf(false) }

        val isOverlayVisible = isMoreOptionsPopupVisible || isConfirmDeletionPopupVisible

        val onCancelFoodDeletion = {
            isConfirmDeletionPopupVisible = false
            isMoreOptionsPopupVisible = true
        }

        val onOverlayClick = {
            if (isMoreOptionsPopupVisible) {
                isMoreOptionsPopupVisible = false
            }

            if (isConfirmDeletionPopupVisible) {
                onCancelFoodDeletion()
            }
        }

        Screen(
            header = {
                Box(
                    content = {
                        Header(
                            onBackClick = {
                                if (foodDeleteState is UiState.Error ||
                                    foodDeleteState is UiState.Success
                                ) {
                                    viewModel.resetFoodDeleteState()
                                }

                                onBackClick()
                            },
                            title = title
                        ) {
                            if (foodDeleteState !is UiState.Success) {
                                FoodMoreOptionsButton(
                                    onClick = {
                                        if (!isMoreOptionsPopupVisible
                                            && !isConfirmDeletionPopupVisible
                                        ) {
                                            isMoreOptionsPopupVisible = true
                                            return@FoodMoreOptionsButton
                                        }

                                        if (isMoreOptionsPopupVisible
                                            && !isConfirmDeletionPopupVisible
                                        ) {
                                            isMoreOptionsPopupVisible = false
                                            return@FoodMoreOptionsButton
                                        }
                                    }
                                )
                            }
                        }
                    }
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                SuccessMessageAnimated(
                    message = "Food deleted successfully",
                    isVisible = foodDeleteState is UiState.Success
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ErrorBannerAnimated(
                        isVisible = foodDeleteErrorMessage != null,
                        text = foodDeleteErrorMessage ?: ""
                    )

                    FoodInformation(
                        food = food,
                        isFoodDeletionSuccess = foodDeleteState is UiState.Success,
                        user = user
                    )
                }

                DarkOverlay(
                    isVisible = isOverlayVisible,
                    onClick = onOverlayClick
                )

                MoreOptionsPopup(
                    isVisible = isMoreOptionsPopupVisible,
                    onEdit = {
                        isMoreOptionsPopupVisible = false
                        onNavigateToEditionScreen()
                    },
                    onDelete = {
                        isConfirmDeletionPopupVisible = true
                        isMoreOptionsPopupVisible = false
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                )

                ConfirmFoodDeletionPopup(
                    isVisible = isConfirmDeletionPopupVisible,
                    onCancel = onCancelFoodDeletion,
                    onConfirm = {
                        viewModel.deleteFood()
                        isConfirmDeletionPopupVisible = false
                        isMoreOptionsPopupVisible = false
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
