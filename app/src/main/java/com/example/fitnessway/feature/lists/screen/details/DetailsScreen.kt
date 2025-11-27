package com.example.fitnessway.feature.lists.screen.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.lists.screen.details.composables.EditionMode
import com.example.fitnessway.feature.lists.screen.details.composables.FoodInformation
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessageAnimated
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodEditionFieldsProvider
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailsScreen(
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

    val foodUpdateErrMsg = when (val state = uiState.foodUpdateState) {
        is UiState.Error -> {
            LaunchedEffect(state) {
                delay(7000)
                viewModel.resetFoodUpdateState()
            }

            state.message
        }

        else -> ""
    }

    val food = selectedFood
    val title = "Food Details"

    if (food == null) {
        Screen(
            header = { Header(onBackClick, title) },
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
            Screen(
                header = {
                    Header(
                        onBackClick = onBackClick,
                        isOnBackEnabled = !formState.isEditing,
                        title = title
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
                            Column(
                                verticalArrangement = Arrangement.spacedBy(18.dp),
                                content = {
                                    ApiErrorMessageAnimated(
                                        isVisible = foodUpdateErrMsg != "",
                                        errorMessage = foodUpdateErrMsg
                                    )

                                    FoodInformation(
                                        food = food,
                                        onEdit = { viewModel.startEditionMode() },
                                        shouldOverlayAppear = formState.isEditing,
                                    )
                                }
                            )

                            AnimatedVisibility(
                                visible = formState.isEditing,
                                enter = slideInVertically(
                                    initialOffsetY = { fullHeight -> fullHeight },
                                    animationSpec = tween(durationMillis = 300)
                                ),
                                exit = slideOutVertically(
                                    targetOffsetY = { fullHeight -> fullHeight },
                                    animationSpec = tween(durationMillis = 300)
                                ),
                                content = {
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
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    }
}
