package com.example.fitnessway.feature.home.screen.logdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.home.screen.logdetails.composables.EditionMode
import com.example.fitnessway.feature.home.screen.logdetails.composables.LogDetails
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.ActionButton
import com.example.fitnessway.ui.shared.ApiErrorMessageAnimated
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Animation.rememberHeaderSlideUpAnimation
import com.example.fitnessway.util.Food.calcNutrientsBasedOnFoodLogServings
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.Ui.AppLabel
import com.example.fitnessway.util.Ui.handleErrStateTempMsg
import com.example.fitnessway.util.form.field.provider.FoodLogEditionFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun LogDetailsScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFoodLog by viewModel.selectedFoodLog.collectAsState()
    val foodLogDetailsFormState by viewModel.foodLogEditionFormState.collectAsState()

    val user = viewModel.user

    LaunchedEffect(selectedFoodLog) {
        selectedFoodLog?.let { foodLog ->
            viewModel.initializeFoodLogEditionForm(foodLog)
        }
    }

    val foodLog = selectedFoodLog
    val title = "Food Log Details"

    val foodLogUpdateErrMsg = handleErrStateTempMsg(
        uiState = uiState.foodLogUpdateState,

        // @NOTE: `onTimeout = { viewModel.resetFoodUpdateState() }` could
        // be used, but since we are only calling 1 function, a function reference
        // suffices
        onTimeOut = viewModel::resetFoodLogUpdateState
    )

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
                                title = "Log Details"
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppLabel(
                                        text = foodLog.category.replaceFirstChar { it.uppercase() },
                                        size = Ui.LabelSize.MEDIUM
                                    )
                                    AppLabel(
                                        text = foodLog.time,
                                        size = Ui.LabelSize.MEDIUM
                                    )

                                    ActionButton(
                                        onClick = { viewModel.startFormEdit(formState.data) },
                                        text = "Edit"
                                    )
                                }
                            }
                        }
                    )
                },
            ) {
                if (user != null) {
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
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.offset(y = headerOffset),
                                content = {
                                    val nutrients = remember(
                                        key1 = foodLog.food.nutrients,
                                        key2 = formState.data.servings
                                    ) {
                                        val servings =
                                            formState.data.servings.toDoubleOrNull() ?: 0.0

                                        calcNutrientsBasedOnFoodLogServings(
                                            nutrients = foodLog.food.nutrients,
                                            currentServings = foodLog.servings,
                                            newServings = servings
                                        )
                                    }

                                    ApiErrorMessageAnimated(
                                        isVisible = foodLogUpdateErrMsg != "",
                                        errorMessage = foodLogUpdateErrMsg
                                    )

                                    LogDetails(
                                        foodLog = foodLog,
                                        isBlurredOverlayVisible = formState.isEditing,
                                        nutrients = nutrients,
                                        user = user
                                    )
                                }
                            )

                            val isValid =
                                viewModel.isFleFormValid && formState.data.servings.toDouble() != foodLog.servings

                            EditionMode(
                                fields = fields,
                                isDoneEnabled = isValid,
                                onDone = viewModel::updateFoodLog,
                                onCancel = { viewModel.cancelFormEdit(formState.data) },
                                isVisible = formState.isEditing
                            )
                        }
                    )
                } else {
                    NotFoundText("User not found")
                }
            }
        }
    }
}
