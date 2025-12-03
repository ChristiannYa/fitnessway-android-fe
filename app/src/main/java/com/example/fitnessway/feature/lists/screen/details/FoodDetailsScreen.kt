package com.example.fitnessway.feature.lists.screen.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.lists.screen.details.composables.ConfirmFoodDeletionPopup
import com.example.fitnessway.feature.lists.screen.details.composables.EditionMode
import com.example.fitnessway.feature.lists.screen.details.composables.FoodInformation
import com.example.fitnessway.feature.lists.screen.details.composables.FoodMoreOptionsButton
import com.example.fitnessway.feature.lists.screen.details.composables.MoreOptionsPopup
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessageAnimated
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.SuccessIcon
import com.example.fitnessway.util.Animation.rememberHeaderSlideUpAnimation
import com.example.fitnessway.util.Ui.handleErrorStateMessage
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodEditionFieldsProvider
import kotlinx.coroutines.delay
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

    DisposableEffect(Unit) {
        onDispose {
            if (uiState.foodUpdateState is UiState.Error) {
                viewModel.resetFoodUpdateState()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (uiState.foodDeleteState !is UiState.Idle) {
                viewModel.resetFoodDeleteState()
            }
        }
    }

    val foodUpdateErrMsg = handleErrorStateMessage(
        uiState = uiState.foodUpdateState,
        onTimeOut = viewModel::resetFoodUpdateState
    )

    val food = selectedFood
    val title = "Food Details"

    if (food == null) {
        Screen(
            header = { Header(onBackClick = onBackClick, title = title) },
            content = {
                Text(
                    text = "No food selected",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    } else {
        foodEditionFormState?.let { formState ->
            val (headerOffset, headerAnimationModifier) = rememberHeaderSlideUpAnimation(
                shouldSlideUp = formState.isEditing
            )

            var isMoreOptionsPopupDisplayed by remember { mutableStateOf(false) }
            var isConfirmDeletionPopupDisplayed by remember { mutableStateOf(false) }

            val shouldOverlayAppear = formState.isEditing ||
                    isMoreOptionsPopupDisplayed ||
                    isConfirmDeletionPopupDisplayed

            fun onCancelFoodDeletion() {
                isConfirmDeletionPopupDisplayed = false
                isMoreOptionsPopupDisplayed = true
            }

            val onOverlayClick = {
                if (isMoreOptionsPopupDisplayed) {
                    isMoreOptionsPopupDisplayed = false
                }

                if (isConfirmDeletionPopupDisplayed) {
                    onCancelFoodDeletion()
                }
            }

            Screen(
                header = {
                    Box(
                        modifier = headerAnimationModifier,
                        content = {
                            Header(
                                onBackClick = onBackClick,
                                isOnBackEnabled = !formState.isEditing,
                                title = title,
                                extraContent = {
                                    FoodMoreOptionsButton(
                                        onClick = {
                                            isMoreOptionsPopupDisplayed =
                                                !isMoreOptionsPopupDisplayed
                                        },
                                        enabled = !shouldOverlayAppear
                                    )
                                }
                            )
                        }
                    )
                },
                content = {
                    val fieldsProvider = FoodEditionFieldsProvider(
                        formState = formState,
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

                    val nutrientFields = nutrients.map { (type, ns, title) ->
                        val fields = ns
                            .filter { it.nutrient.id !in deletedNutrients }
                            .map { fieldsProvider.nutrient(it.nutrient) }

                        Triple(type, fields, title)
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        content = {
                            if (uiState.foodDeleteState is UiState.Success) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth(),
                                    content = {
                                        Text(
                                            text = "Food deleted successfully",
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        SuccessIcon()
                                    }
                                )
                            } else {
                                MoreOptionsPopup(
                                    isVisible = isMoreOptionsPopupDisplayed,
                                    onEdit = {
                                        isMoreOptionsPopupDisplayed = false
                                        viewModel.startEditionMode()
                                    },
                                    onDelete = {
                                        isConfirmDeletionPopupDisplayed = true
                                        isMoreOptionsPopupDisplayed = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .zIndex(2f)
                                )

                                ConfirmFoodDeletionPopup(
                                    isVisible = isConfirmDeletionPopupDisplayed,
                                    onCancel = { onCancelFoodDeletion() },
                                    onConfirm = { viewModel.deleteFood() }
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(18.dp),
                                    modifier = Modifier.offset(y = headerOffset),
                                    content = {
                                        ApiErrorMessageAnimated(
                                            isVisible = foodUpdateErrMsg != "",
                                            errorMessage = foodUpdateErrMsg
                                        )

                                        FoodInformation(
                                            food = food,
                                            shouldOverlayAppear = shouldOverlayAppear,
                                            onOverlayClick = onOverlayClick
                                        )
                                    }
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
                                    onCancel = {
                                        viewModel.cancelEditionMode()
                                    },
                                    onRemoveNutrient = { nutrientId ->
                                        viewModel.filterNutrientFromForm(nutrientId)
                                    },
                                    isVisible = formState.isEditing
                                )
                            }
                        }
                    )
                }
            )
        }
    }
}
