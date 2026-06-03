package com.example.fitnessway.feature.profile.screen.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.mapnbt
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.mappers.toListByType
import com.example.fitnessway.data.mappers.toSuccessOrNull
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.feature.profile.screen.goals.composables.NutrientFields
import com.example.fitnessway.feature.profile.screen.goals.composables.UpgradePromptSection
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.util.Ui.handleTempApiErrMsg
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.NutrientGoalsFieldsProvider
import org.koin.androidx.compose.koinViewModel


@Composable
fun ProfileGoalsScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userRepoUiState by viewModel.userRepoUiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val goalsEditionFormState by viewModel.goalsEditionFormState.collectAsState()
    val isGoalsFormValid by viewModel.isGoalsFormValid.collectAsState()

    val user = userRepoUiState.userUiState.toSuccessOrNull()
    val nutrientsUiState = nutrientRepoUiState.nutrients
    val nutrientGoalsSetUiState = uiState.nutrientGoalsSetUiState

    val nutrientGoalsSetErrorMessage = handleTempApiErrMsg(
        uiState = nutrientGoalsSetUiState,
        onTimeOut = viewModel::resetNutrientGoalsUpdateState
    )

    val title = "My Goals"
    val goalsEditionFormStateCopy = goalsEditionFormState

    val scrollState = rememberScrollState()

    if (user != null) {
        Screen(
            header = {
                Header(
                    onBackClick = {
                        if (nutrientGoalsSetUiState is UiState.Error) {
                            viewModel.resetNutrientGoalsUpdateState()
                        }

                        onBackClick()
                    },
                    isOnBackEnabled = nutrientGoalsSetUiState !is UiState.Loading,
                    title = "My Goals"
                ) {
                    Clickables.DoneButton(
                        onClick = {
                            viewModel.setGoalsThatChanged()
                            viewModel.setNutrientGoals()
                        },
                        enabled = isGoalsFormValid,
                        isLoading = nutrientGoalsSetUiState is UiState.Loading
                    )
                }
            }
        ) { focusManager ->
            if (nutrientsUiState is UiState.Success && goalsEditionFormStateCopy != null) {
                val nutrients = nutrientsUiState.data

                val goalFields = NutrientType.entries.associateWith { type ->
                    val nutrientsByType = remember(
                        key1 = nutrients,
                        key2 = user.isPremium
                    ) {
                        nutrients
                            .toListByType(type)
                            .filter { n -> user.isPremium || !n.base.isPremium }
                    }

                    val fieldsProvider = remember(
                        goalsEditionFormStateCopy,
                        nutrientGoalsSetUiState
                    ) {
                        NutrientGoalsFieldsProvider(
                            formState = goalsEditionFormStateCopy,
                            isFormSubmitting = nutrientGoalsSetUiState is UiState.Loading,
                            focusManager = focusManager,
                            onFieldUpdate = { fieldName, value ->
                                viewModel.updateGoalEditionFormField(
                                    fieldName = fieldName,
                                    input = value
                                )
                            }
                        )
                    }

                    nutrientsByType.map { nutrientDataByType ->
                        fieldsProvider.nutrientGoal(
                            nutrientData = nutrientDataByType,
                            isLastField = nutrients
                                .toList()
                                .map { nutrientData -> nutrientData.base }
                                .last() == nutrientDataByType.base
                        )
                    }
                }

                val premiumNutrientsMap = NutrientType.entries.associateWith { type ->
                    nutrientsUiState.data
                        .mapnbt { _, nutrients ->
                            nutrients.filter { nutrient ->
                                user.isPremium || nutrient.base.isPremium
                            }
                        }
                        .toListByType(type)
                        .map { it.base }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.imePadding()
                ) {
                    ErrorBannerAnimated(
                        isVisible = nutrientGoalsSetErrorMessage != null,
                        text = nutrientGoalsSetErrorMessage ?: ""
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.verticalScroll(scrollState)
                    ) {
                        NutrientFields(goalFields)

                        if (!user.isPremium) UpgradePromptSection(premiumNutrientsMap)
                    }
                }
            }
        }

    } else NotFoundScreen(
        onBackClick = onBackClick,
        message = "User not found",
        title = title
    )
}