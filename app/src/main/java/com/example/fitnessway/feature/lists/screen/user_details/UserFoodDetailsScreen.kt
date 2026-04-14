package com.example.fitnessway.feature.lists.screen.user_details

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
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
import com.example.fitnessway.data.mappers.toM26FoodInformation
import com.example.fitnessway.feature.lists.screen.user_details.composables.ConfirmFoodDeletionPopup
import com.example.fitnessway.feature.lists.screen.user_details.composables.FoodInformation
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.DarkOverlay
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Messages.SuccessMessageAnimated
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.util.Ui.handleTempApiErrMsg
import com.example.fitnessway.util.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserFoodDetailsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onNavigateToEditionScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val selectedFood by viewModel.editionManager.selectedFood.collectAsState()

    val user = userFlow
    val selectedFoodCopy = selectedFood

    val foodDeleteState = uiState.foodDeleteState

    val foodDeleteErrorMessage = handleTempApiErrMsg(
        uiState = foodDeleteState,
        onTimeOut = viewModel::resetFoodDeleteState
    )

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
                        if (foodDeleteState !is UiState.Idle) viewModel.resetFoodDeleteState()
                        onBackClick()
                    },
                    title = "Food Details"
                ) {
                    if (foodDeleteState !is UiState.Success) {

                        Clickables.AppPngIconButton(
                            icon = Structure.AppIconSource.Vector(Icons.Default.MoreHoriz),
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
                        food = selectedFoodCopy.toM26FoodInformation(),
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
                            viewModel.editionManager.initializeFoodForm(selectedFoodCopy)
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
    }
}