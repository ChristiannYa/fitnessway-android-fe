package com.example.fitnessway.feature.lists.screen.details

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.lists.screen.details.composables.ConfirmFoodDeletionPopup
import com.example.fitnessway.feature.lists.screen.details.composables.FoodInformation
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.DarkOverlay
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Messages.SuccessMessageAnimated
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.util.Ui.handleTempApiErrorMessage
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.isIdle
import com.example.fitnessway.util.isSuccess
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodDetailsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onNavigateToEditionScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val selectedFood by viewModel.selectedFood.collectAsState()

    val user = userFlow
    val selectedFoodCopy = selectedFood

    val foodDeleteState = uiState.foodDeleteState
    val foodFavoriteStatusUpdateState = uiState.foodFavoriteStatusUpdateState

    val foodDeleteErrorMessage = handleTempApiErrorMessage(
        uiState = foodDeleteState,
        onTimeOut = viewModel::resetFoodDeleteState
    )

    val title = "Food Details"
    val view = LocalView.current

    if (selectedFoodCopy != null && user != null) {
        val moreOptionsState = Structure.rememberMoreOptionsState()
        var isConfirmDeletionPopupVisible by remember { mutableStateOf(false) }
        val isOverlayVisible = moreOptionsState.isVisible || isConfirmDeletionPopupVisible

        fun onCancelFoodDeletion() {
            isConfirmDeletionPopupVisible = false
            moreOptionsState.show()
        }

        fun onOverlayClick() {
            if (moreOptionsState.isVisible) moreOptionsState.hide()
            if (isConfirmDeletionPopupVisible) onCancelFoodDeletion()
        }

        Screen(
            header = {
                Header(
                    onBackClick = {
                        if (!foodDeleteState.isIdle) viewModel.resetFoodDeleteState()
                        if (!foodFavoriteStatusUpdateState.isIdle) viewModel.resetFoodFavoriteStatusUpdateState()

                        onBackClick()
                    },
                    title = title
                ) {
                    val isFavorite = selectedFoodCopy.metadata.isFavorite

                    if (!foodDeleteState.isSuccess) {
                        val favoriteIcon = if (isFavorite) {
                            Icons.Default.Star
                        } else Icons.Default.StarBorder

                        Clickables.AppPngIconButton(
                            icon = Structure.AppIconButtonSource.Vector(favoriteIcon),
                            contentDescription = "Favorite this food",
                            enabled = !(moreOptionsState.isVisible || isConfirmDeletionPopupVisible),
                            onClick = {
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                viewModel.updateFoodFavoriteStatus(!isFavorite)
                            }
                        )

                        Clickables.AppPngIconButton(
                            icon = Structure.AppIconButtonSource.Vector(Icons.Default.MoreHoriz),
                            contentDescription = "More options",
                            onClick = {
                                view.playSoundEffect(SoundEffectConstants.CLICK)

                                if (!moreOptionsState.isVisible
                                    && !isConfirmDeletionPopupVisible
                                ) {
                                    moreOptionsState.show()
                                    return@AppPngIconButton
                                }

                                if (moreOptionsState.isVisible
                                    && !isConfirmDeletionPopupVisible
                                ) {
                                    moreOptionsState.hide()
                                    return@AppPngIconButton
                                }

                                if (!moreOptionsState.isVisible
                                    && isConfirmDeletionPopupVisible
                                ) {
                                    isConfirmDeletionPopupVisible = false
                                    moreOptionsState.show()
                                }
                            }
                        )
                    }
                }
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
                        food = selectedFoodCopy,
                        isFoodDeletionSuccess = foodDeleteState is UiState.Success,
                        user = user
                    )
                }

                DarkOverlay(
                    isVisible = isOverlayVisible,
                    onClick = ::onOverlayClick
                )

                Structure.MoreOptions(
                    state = moreOptionsState,
                    Structure.MoreOptionsConfig(
                        text = "Edit",
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            moreOptionsState.hide()
                            viewModel.initializeFoodForm(selectedFoodCopy)
                            onNavigateToEditionScreen()
                        },
                        backgroundColor = MaterialTheme.colorScheme.primary
                    ),
                    Structure.MoreOptionsConfig(
                        text = "Delete",
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            isConfirmDeletionPopupVisible = true
                            moreOptionsState.hide()
                        }
                    ),
                    modifier = Modifier.align(Alignment.TopEnd)
                )

                ConfirmFoodDeletionPopup(
                    isVisible = isConfirmDeletionPopupVisible,
                    onCancel = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        onCancelFoodDeletion()
                    },
                    onConfirm = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        isConfirmDeletionPopupVisible = false
                        moreOptionsState.hide()
                        viewModel.deleteFood()
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    } else NotFoundScreen(
        onBackClick = onBackClick,
        title = title,
        message = "Food data not found"
    )
}