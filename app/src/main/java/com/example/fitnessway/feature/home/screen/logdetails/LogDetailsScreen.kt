package com.example.fitnessway.feature.home.screen.logdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.fitnessway.feature.home.screen.logdetails.composables.EditionMode
import com.example.fitnessway.feature.home.screen.logdetails.composables.LogDetails
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessageAnimated
import com.example.fitnessway.ui.shared.EditButton
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Animation.rememberHeaderSlideUpAnimation
import com.example.fitnessway.util.form.field.provider.FoodLogEditionFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun LogDetailsScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val selectedFoodLog by viewModel.selectedFoodLog.collectAsState()
    val foodLogDetailsFormState by viewModel.foodLogEditionFormState.collectAsState()

    LaunchedEffect(selectedFoodLog) {
        selectedFoodLog?.let { foodLog ->
            viewModel.initializeFoodLogEditionForm(foodLog)
        }
    }

    val foodLog = selectedFoodLog
    val title = "Food Log Details"

    if (foodLog == null) {
        Screen(
            header = { Header(onBackClick = onBackClick, title = title) },
            content = {
                Text(
                    text = "Food log not found",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    } else {
        foodLogDetailsFormState?.let { formState ->
            val (headerOffset, headerAnimationModifier) = rememberHeaderSlideUpAnimation(
                shouldSlideUp = formState.isEditing
            )

            Screen(
                header = {
                    Box(
                        modifier = headerAnimationModifier,
                        content = {
                            Header(
                                onBackClick = onBackClick,
                                isOnBackEnabled = !formState.isEditing,
                                title = "Log Details",
                                extraContent = {
                                    EditButton(
                                        onClick = { viewModel.startFormEdit(formState.data) }
                                    )
                                }
                            )
                        }
                    )
                },

                content = {
                    val fieldsProvider = FoodLogEditionFieldsProvider(
                        formState = formState,
                        onFieldUpdate = { fieldName, value ->
                            viewModel.updateFoodLogEditionFormField(
                                fieldName = fieldName,
                                input = value
                            )
                        }
                    )

                    val fields = listOf(
                        fieldsProvider.servings(),
                        fieldsProvider.amountPerServing(
                            servingUnit = foodLog.food.information.servingUnit
                        )
                    )

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        content = {
                            Column(
                                modifier = Modifier.offset(y = headerOffset),
                                content = {
                                    ApiErrorMessageAnimated(
                                        isVisible = false,
                                        errorMessage = "Food log edition error"
                                    )

                                    LogDetails(
                                        foodLog = foodLog,
                                        isBlurredOverlayVisible = formState.isEditing
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
                                    val isValid = viewModel.isFleFormValid &&
                                            formState.data.servings.toDouble() != foodLog.servings

                                    EditionMode(
                                        fields = fields,
                                        isDoneEnabled = isValid,
                                        onDone = {
                                            viewModel.updateFoodLog()
                                        },
                                        onCancel = {
                                            viewModel.cancelFormEdit(formState.data)
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
