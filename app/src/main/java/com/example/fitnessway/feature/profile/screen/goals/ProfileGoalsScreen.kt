package com.example.fitnessway.feature.profile.screen.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.profile.screen.goals.composables.NutrientGoalsContent
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.ActionButton
import com.example.fitnessway.ui.shared.ApiErrorMessageAnimated
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.TextWithLoadingIndicator
import com.example.fitnessway.util.Nutrient.filterNutrientsByType
import com.example.fitnessway.util.Nutrient.filterOutNonPremiumNutrients
import com.example.fitnessway.util.Nutrient.filterOutPremiumNutrients
import com.example.fitnessway.util.Nutrient.mapNutrients
import com.example.fitnessway.util.Ui.handleErrStateTempMsg
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.NutrientGoalsFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileGoalsScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val goalsEditionFormState by viewModel.goalsEditionFormState.collectAsState()
    val isGoalsFormValid by viewModel.isGoalsFormValid.collectAsState()

    val nutrientsState = nutrientRepoUiState.nutrientsUiState
    val user = viewModel.user

    val nutrientGoalsUpdateErrMsg = handleErrStateTempMsg(
        uiState = uiState.nutrientGoalsSetUiState,
        onTimeOut = viewModel::resetNutrientGoalsUpdateState
    )

    LaunchedEffect(nutrientsState) {
        if (nutrientsState is UiState.Success) {
            viewModel.initNutrientGoalsForm(
                nutrientsData = nutrientsState.data
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getNutrients()
    }

    Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                title = "My Goals",
                extraContent = {
                    ActionButton(
                        onClick = {
                            viewModel.setGoalsThatChanged()
                            viewModel.setNutrientGoals()
                        },
                        text = "Update",
                        enabled = isGoalsFormValid
                    )
                }
            )
        },

        content = {
            if (user != null) {
                when (nutrientsState) {
                    is UiState.Loading -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize(),
                            content = {
                                TextWithLoadingIndicator("Loading nutrients data")
                            }
                        )
                    }

                    is UiState.Success -> {
                        goalsEditionFormState?.let { formState ->
                            val goalFields = remember(nutrientsState.data, formState) {
                                val fieldsProvider = NutrientGoalsFieldsProvider(
                                    formState = formState,
                                    onFieldUpdate = { fieldName, value ->
                                        viewModel.updateGoalEditionFormField(
                                            fieldName = fieldName,
                                            input = value
                                        )
                                    }
                                )

                                val fieldsByTypeMap = NutrientType.entries.associateWith { type ->
                                    filterNutrientsByType(nutrientsState.data, type)
                                        .filterOutPremiumNutrients(user.isPremium)
                                        .map { fieldsProvider.nutrientGoal(it) }
                                }

                                fieldsByTypeMap
                            }

                            val premiumNutrientsMap = NutrientType.entries.associateWith { type ->
                                filterNutrientsByType(
                                    nutrients = nutrientsState.data.mapNutrients {
                                        it.filterOutNonPremiumNutrients(user.isPremium)
                                    },
                                    type = type
                                )
                                    .map { it.nutrient }
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                content = {
                                    ApiErrorMessageAnimated(
                                        isVisible = nutrientGoalsUpdateErrMsg != "",
                                        errorMessage = nutrientGoalsUpdateErrMsg
                                    )

                                    NutrientGoalsContent(
                                        nutrientFields = goalFields,
                                        premiumNutrientsMap = premiumNutrientsMap,
                                        user = user
                                    )
                                }
                            )
                        } ?: NotFoundText(text = "Form data not found")
                    }

                    else -> NotFoundText(text = "Something went wrong")
                }
            } else {
                NotFoundText("User not found")
            }
        }
    )
}