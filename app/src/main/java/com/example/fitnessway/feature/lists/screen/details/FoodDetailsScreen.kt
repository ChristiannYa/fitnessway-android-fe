package com.example.fitnessway.feature.lists.screen.details

import android.view.SoundEffectConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
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
import com.example.fitnessway.feature.lists.screen.details.composables.FoodMoreOptionsButton
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.DarkOverlay
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.HeaderRow
import com.example.fitnessway.ui.shared.Messages.SuccessMessageAnimated
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.util.Animation
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

    val userCopy = viewModel.user
    val selectedFoodCopy = selectedFood
    val foodDeleteState = uiState.foodDeleteState
    val title = "Food Details"

    val foodDeleteErrorMessage = handleTempApiErrorMessage(
        uiState = foodDeleteState,
        onTimeOut = viewModel::resetFoodDeleteState
    )

    val view = LocalView.current

    if (selectedFoodCopy == null || userCopy == null) {
        NotFoundScreen(
            onBackClick = onBackClick,
            title = title,
            message = "Food data not found"
        )
    } else {
        val moreOptionsState = Structure.rememberMoreOptionsState()
        var isConfirmDeletionPopupVisible by remember { mutableStateOf(false) }
        val isOverlayVisible = moreOptionsState.isVisible || isConfirmDeletionPopupVisible

        val onCancelFoodDeletion = {
            isConfirmDeletionPopupVisible = false
            moreOptionsState.show()
        }

        val onOverlayClick = {
            if (moreOptionsState.isVisible) {
                moreOptionsState.hide()
            }

            if (isConfirmDeletionPopupVisible) {
                onCancelFoodDeletion()
            }
        }

        Screen(
            header = {
                Header(
                    onBackClick = {
                        if (foodDeleteState is UiState.Error ||
                            foodDeleteState is UiState.Success
                        ) { viewModel.resetFoodDeleteState() }

                        onBackClick()
                    },
                    title = title
                ) {
                    val isFavorite = selectedFoodCopy.metadata.isFavorite

                    if (foodDeleteState !is UiState.Success) {
                        HeaderRow {
                            val favoriteIcon = if (isFavorite) {
                                Icons.Default.Star
                            } else Icons.Default.StarBorder

                            val favoriteIconTint by animateColorAsState(
                                targetValue = if (isFavorite) {
                                    MaterialTheme.colorScheme.primary
                                } else MaterialTheme.colorScheme.onSurfaceVariant,
                                animationSpec = Animation.colorSpec,
                                label = "favoriteIconTint_ColorSpec"
                            )

                            Clickables.AppIconButton(
                                icon = Structure.AppIconButtonSource.Vector(favoriteIcon),
                                contentDescription = "Favorite this food",
                                enabled = !(moreOptionsState.isVisible || isConfirmDeletionPopupVisible),
                                onClick = {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    viewModel.updateFoodFavoriteStatus(!isFavorite)
                                },
                                iconTint = favoriteIconTint
                            )

                            FoodMoreOptionsButton(
                                onClick = {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)

                                    if (!moreOptionsState.isVisible
                                        && !isConfirmDeletionPopupVisible
                                    ) {
                                        moreOptionsState.show()
                                        return@FoodMoreOptionsButton
                                    }

                                    if (moreOptionsState.isVisible
                                        && !isConfirmDeletionPopupVisible
                                    ) {
                                        moreOptionsState.hide()
                                        return@FoodMoreOptionsButton
                                    }

                                    if (!moreOptionsState.isVisible
                                        && isConfirmDeletionPopupVisible) {
                                        isConfirmDeletionPopupVisible = false
                                        moreOptionsState.show()
                                    }
                                }
                            )
                        }
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
                        user = userCopy
                    )
                }

                DarkOverlay(
                    isVisible = isOverlayVisible,
                    onClick = onOverlayClick
                )

                Structure.MoreOptions(
                    state = moreOptionsState,
                    Structure.MoreOptionsConfig(
                        text = "Edit",
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            moreOptionsState.hide()
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