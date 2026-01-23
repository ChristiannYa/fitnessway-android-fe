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
import com.example.fitnessway.data.model.MNutrient.Enum.NutrientType
import com.example.fitnessway.feature.profile.screen.goals.composables.NutrientFields
import com.example.fitnessway.feature.profile.screen.goals.composables.UpgradePromptSection
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.util.UNutrient.combine
import com.example.fitnessway.util.UNutrient.filterNonPremiumPreferences
import com.example.fitnessway.util.UNutrient.filterNutrientsByType
import com.example.fitnessway.util.UNutrient.filterPremium
import com.example.fitnessway.util.UNutrient.mapNutrients
import com.example.fitnessway.util.Ui.handleTempApiErrorMessage
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.NutrientGoalsFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileGoalsScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val goalsEditionFormState by viewModel.goalsEditionFormState.collectAsState()
    val isGoalsFormValid by viewModel.isGoalsFormValid.collectAsState()

    val user = userFlow
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState
    val nutrientGoalsSetUiState = uiState.nutrientGoalsSetUiState

    val nutrientGoalsSetErrorMessage = handleTempApiErrorMessage(
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
                    Clickables.HeaderDoneButton(
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
                            .filterNutrientsByType(type)
                            .filterNonPremiumPreferences(user.isPremium)
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
                        val isLastField = nutrients.combine().map { nutrientData ->
                            nutrientData.nutrient
                        }.last() == nutrientDataByType.nutrient

                        fieldsProvider.nutrientGoal(
                            nutrientData = nutrientDataByType,
                            isLastField = isLastField
                        )
                    }
                }

                val premiumNutrientsMap = NutrientType.entries.associateWith { type ->
                    nutrientsUiState.data
                        .mapNutrients { _, nutrients -> nutrients.filterPremium(user.isPremium) }
                        .filterNutrientsByType(type)
                        .map { it.nutrient }
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
                        NutrientFields(
                            nutrientFields = goalFields,
                            isUserPremium = user.isPremium
                        )

                        if (!user.isPremium) {
                            UpgradePromptSection(
                                premiumNutrientsMap = premiumNutrientsMap
                            )
                        }
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