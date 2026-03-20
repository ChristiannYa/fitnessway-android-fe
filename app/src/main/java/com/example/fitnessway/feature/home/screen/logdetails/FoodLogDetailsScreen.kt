package com.example.fitnessway.feature.home.screen.logdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MFood.Enum.FoodLogFoodStatus
import com.example.fitnessway.feature.home.screen.logdetails.composables.FoodLogDetails
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.util.UFood.calcNutrientIntakesFromFoodLogServings
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.Ui.AppLabel
import com.example.fitnessway.util.Ui.handleTempApiErrMsg
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodLogEditionFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodLogDetailsScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val selectedFoodLog by viewModel.selectedFoodLog.collectAsState()
    val foodLogDetailsFormState by viewModel.foodLogEditionFormState.collectAsState()

    val user = userFlow
    val foodLog = selectedFoodLog
    val foodLogUpdateState = uiState.foodLogUpdateState
    val focusManager = LocalFocusManager.current

    val foodLogUpdateErrMsg = handleTempApiErrMsg(
        uiState = foodLogUpdateState,
        onTimeOut = viewModel::resetFoodLogUpdateState
    )

    LaunchedEffect(selectedFoodLog) {
        selectedFoodLog?.let { foodLog ->
            viewModel.initializeFoodLogEditionForm(foodLog)
        }
    }

    if (foodLog == null) {
        Screen(
            header = { Header(onBackClick = onBackClick, title = "Food Log Details") }
        ) {
            NotFoundMessage("Food log not found")
        }
    } else {
        foodLogDetailsFormState?.let { formState ->
            Screen(
                header = {
                    Header(
                        onBackClick = {
                            if (foodLogUpdateState is UiState.Error) {
                                viewModel.resetFoodLogUpdateState()
                            }

                            onBackClick()
                        },
                        isOnBackEnabled = !formState.isEditing,
                        title = "Log Details"
                    ) {
                        if (foodLog.foodStatus == FoodLogFoodStatus.PRESENT &&
                            foodLog.food.metadata.isFavorite
                        ) {
                            Structure.AppIconDynamic(
                                source = Structure.AppIconButtonSource.Vector(
                                    imageVector = Icons.Default.Star
                                ),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AppLabel<Unit>(
                            text = foodLog.category.replaceFirstChar { it.uppercase() },
                            size = Ui.LabelSize.SMALL
                        )

                        AppLabel<Unit>(
                            text = foodLog.time,
                            size = Ui.LabelSize.SMALL
                        )

                        Clickables.HeaderDoneButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.updateFoodLog()
                            },
                            enabled = viewModel.isFoodLogEditionFormValid
                        )
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
                        },
                        focusManager = focusManager
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

                            ErrorBannerAnimated(
                                isVisible = foodLogUpdateErrMsg != null,
                                text = foodLogUpdateErrMsg ?: ""
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
                } else NotFoundMessage("User not found")
            }
        }
    }
}
