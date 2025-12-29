package com.example.fitnessway.feature.profile.screen.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.profile.screen.goals.composables.NutrientGoalsContent
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.ActionButton
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Loading.LoadingArea
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Formatters.formatUiErrorMessage
import com.example.fitnessway.util.UNutrient.filterNutrientsByType
import com.example.fitnessway.util.UNutrient.filterOutNonPremiumNutrients
import com.example.fitnessway.util.UNutrient.filterOutPremiumNutrients
import com.example.fitnessway.util.UNutrient.mapNutrients
import com.example.fitnessway.util.Ui.handleApiErrorTempMessage
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

    val nutrientGoalsUpdateErrMsg = handleApiErrorTempMessage(
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
                    if (nutrientsState is UiState.Success) {
                        ActionButton(
                            onClick = {
                                viewModel.setGoalsThatChanged()
                                viewModel.setNutrientGoals()
                            },
                            text = "Update",
                            enabled = isGoalsFormValid
                        )
                    }
                }
            )
        },

        content = {
            if (user != null) {
                when (nutrientsState) {
                    is UiState.Loading -> LoadingArea("Loading nutrient goals")

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
                                    nutrients = nutrientsState.data.mapNutrients { _, nutrients ->
                                        nutrients.filterOutNonPremiumNutrients(user.isPremium)
                                    },
                                    type = type
                                )
                                    .map { it.nutrient }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                ErrorBannerAnimated(
                                    isVisible = nutrientGoalsUpdateErrMsg != null,
                                    text = nutrientGoalsUpdateErrMsg ?: ""
                                )

                                NutrientGoalsContent(
                                    nutrientFields = goalFields,
                                    premiumNutrientsMap = premiumNutrientsMap,
                                    isUserPremium = user.isPremium
                                )
                            }
                        } ?: LoadingArea()
                    }

                    else -> {}
                }

                NotFoundMessageAnimated(
                    isVisible = nutrientsState is UiState.Error,
                    message = formatUiErrorMessage(nutrientsState)
                )

            } else NotFoundMessage("User not found")
        }
    )
}