package com.example.fitnessway.feature.lists.screen.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.lists.screen.details.composables.ConfirmFoodDeletionPopup
import com.example.fitnessway.feature.lists.screen.details.composables.EditionMode
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
import com.example.fitnessway.util.Animation.rememberHeaderSlideUpAnimation
import com.example.fitnessway.util.UNutrient.combine
import com.example.fitnessway.util.Ui.handleTempApiErrorMessage
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodEditionFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodDetailsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFood by viewModel.selectedFood.collectAsState()
    val foodEditionFormState by viewModel.foodEditionFormState.collectAsState()
    val deletedNutrients by viewModel.deletedNutrients.collectAsState()

    LaunchedEffect(selectedFood) {
        selectedFood?.let { food ->
            viewModel.initializeFoodForm(food)
        }
    }

    val user = viewModel.user
    val food = selectedFood
    val foodUpdateState = uiState.foodUpdateState
    val foodDeleteState = uiState.foodDeleteState
    val title = "Food Details"

    val foodUpdateErrorMessage = handleTempApiErrorMessage(
        uiState = foodUpdateState,
        onTimeOut = viewModel::resetFoodUpdateState
    )

    val foodDeleteErrorMessage = handleTempApiErrorMessage(
        uiState = foodDeleteState,
        onTimeOut = viewModel::resetFoodDeleteState
    )

    val focusManager = LocalFocusManager.current

    DisposableEffect(Unit) {
        onDispose {
            if (foodUpdateState is UiState.Error) {
                viewModel.resetFoodUpdateState()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (foodDeleteState !is UiState.Idle) {
                viewModel.resetFoodDeleteState()
            }
        }
    }

    if (food == null || user == null) {
        Screen(
            header = { Header(onBackClick = onBackClick, title = title) },
            content = { NotFoundMessage("Food data not found") }
        )
    } else {
        foodEditionFormState?.let { formState ->
            val (headerOffset, headerAnimationModifier) = rememberHeaderSlideUpAnimation(
                shouldSlideUp = formState.isEditing
            )

            var isMoreOptionsPopupVisible by remember { mutableStateOf(false) }
            var isConfirmDeletionPopupVisible by remember { mutableStateOf(false) }

            val isOverlayVisible = formState.isEditing ||
                    isMoreOptionsPopupVisible ||
                    isConfirmDeletionPopupVisible

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

                if (formState.isEditing) {
                    viewModel.cancelEditionMode()
                }
            }

            Screen(
                header = {
                    Box(
                        modifier = headerAnimationModifier,
                        content = {
                            Header(
                                onBackClick = {
                                    if (foodDeleteState is UiState.Error ||
                                        foodDeleteState is UiState.Success
                                    ) {
                                        viewModel.resetFoodDeleteState()
                                    }

                                    if (foodUpdateState is UiState.Error) {
                                        viewModel.resetFoodUpdateState()
                                    }

                                    onBackClick()
                                },
                                isOnBackEnabled = !formState.isEditing,
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
                val fieldsProvider = FoodEditionFieldsProvider(
                    formState = formState,
                    focusManager = focusManager,
                    isFormSubmitting = foodUpdateState is UiState.Loading,
                    onFieldUpdate = { fieldName, value ->
                        viewModel.updateFoodEditionFormField(
                            fieldName = fieldName,
                            input = value
                        )
                    }
                )

                val detailFields = listOf(
                    fieldsProvider.name(),
                    fieldsProvider.brand(),
                    fieldsProvider.amountPerServing(),
                    fieldsProvider.servingUnit()
                )

                val nutrients = listOf(
                    Triple(
                        NutrientType.BASIC,
                        food.nutrients.basic,
                        "Summary"
                    ),
                    Triple(
                        NutrientType.VITAMIN,
                        food.nutrients.vitamin,
                        "Vitamins"
                    ),
                    Triple(
                        NutrientType.MINERAL,
                        food.nutrients.mineral,
                        "Minerals"
                    )
                )

                val allNutrientIds = food.nutrients.combine().map {
                    it.nutrientWithPreferences.nutrient.id
                }
                    .filter { it !in deletedNutrients }

                val nutrientFields = nutrients.map { (type, ns, title) ->
                    val fields = ns
                        .filter { it.nutrientWithPreferences.nutrient.id !in deletedNutrients }
                        .map { nutrientData ->
                            val nutrient = nutrientData.nutrientWithPreferences.nutrient

                            fieldsProvider.nutrient(
                                nutrient = nutrientData.nutrientWithPreferences.nutrient,
                                isLastField = nutrient.id == allNutrientIds.last()
                            )
                        }

                    Triple(type, fields, title)
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    SuccessMessageAnimated(
                        message = "Food deleted successfully",
                        isVisible = foodDeleteState is UiState.Success
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.offset(y = headerOffset)
                    ) {
                        ErrorBannerAnimated(
                            isVisible = foodUpdateErrorMessage != null,
                            text = foodUpdateErrorMessage ?: ""
                        )

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
                        onClick = onOverlayClick,
                        modifier = Modifier.offset(y = headerOffset)
                    )

                    MoreOptionsPopup(
                        isVisible = isMoreOptionsPopupVisible,
                        onEdit = {
                            isMoreOptionsPopupVisible = false
                            viewModel.startEditionMode()
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

                    EditionMode(
                        foodDetailFields = detailFields,
                        nutrientFields = nutrientFields,
                        enabled = viewModel.isFormValid,
                        onDone = {
                            viewModel.simpleFormCancel()
                            viewModel.updateFood()
                            viewModel.resetDeletedNutrients()
                        },
                        onCancel = viewModel::cancelEditionMode,
                        onRemoveNutrient = viewModel::filterNutrientFromForm,
                        isVisible = formState.isEditing
                    )
                }
            }
        }
    }
}
