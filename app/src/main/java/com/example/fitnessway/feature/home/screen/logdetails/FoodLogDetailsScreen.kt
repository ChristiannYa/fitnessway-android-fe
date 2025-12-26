package com.example.fitnessway.feature.home.screen.logdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.home.screen.logdetails.composables.FoodLogDetails
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessageAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Food.calcNutrientIntakesFromFoodLogServings
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.Ui.AppLabel
import com.example.fitnessway.util.Ui.handleErrStateTempMsg
import com.example.fitnessway.util.form.field.provider.FoodLogEditionFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodLogDetailsScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFoodLog by viewModel.selectedFoodLog.collectAsState()
    val foodLogDetailsFormState by viewModel.foodLogEditionFormState.collectAsState()

    val user = viewModel.user

    val foodLog = selectedFoodLog
    val title = "Food Log Details"

    val foodLogUpdateErrMsg = handleErrStateTempMsg(
        uiState = uiState.foodLogUpdateState,
        onTimeOut = viewModel::resetFoodLogUpdateState
    )

    LaunchedEffect(selectedFoodLog) {
        selectedFoodLog?.let { foodLog ->
            viewModel.initializeFoodLogEditionForm(foodLog)
        }
    }

    if (foodLog == null) {
        Screen(
            header = { Header(onBackClick = onBackClick, title = title) }
        ) {
            NotFoundText("Food log not found")
        }
    } else {
        foodLogDetailsFormState?.let { formState ->
            Screen(
                header = {
                    Header(
                        onBackClick = onBackClick,
                        isOnBackEnabled = !formState.isEditing,
                        title = "Log Details"
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
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

                            Clickables.HeaderDoneButton(
                                onClick = viewModel::updateFoodLog,
                                enabled = viewModel.isFoodLogEditionFormValid
                            )
                        }
                    }
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

                    Box(modifier = Modifier.imePadding()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val nutrients = remember(
                                key1 = foodLog.food.nutrients,
                                key2 = formState.data.servings
                            ) {
                                val servings = formState.data.servings.toDoubleOrNull() ?: 0.0

                                calcNutrientIntakesFromFoodLogServings(
                                    nutrients = foodLog.food.nutrients,
                                    currentServings = foodLog.servings,
                                    newServings = servings
                                )
                            }

                            ApiErrorMessageAnimated(
                                isVisible = foodLogUpdateErrMsg != "",
                                errorMessage = foodLogUpdateErrMsg
                            )

                            FoodLogDetails(
                                foodLog = foodLog,
                                isBlurredOverlayVisible = formState.isEditing,
                                nutrients = nutrients,
                                servingField = fieldsProvider.servings(),
                                amountPerServingField = fieldsProvider.amountPerServing(
                                    servingUnit = foodLog.food.information.servingUnit
                                ),
                                user = user
                            )
                        }
                    }
                } else {
                    NotFoundText("User not found")
                }
            }
        }
    }
}
