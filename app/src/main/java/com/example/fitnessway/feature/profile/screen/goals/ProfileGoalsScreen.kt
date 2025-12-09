package com.example.fitnessway.feature.profile.screen.goals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.profile.screen.goals.composables.NutrientGoalsContent
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.ActionButton
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.TextWithLoadingIndicator
import com.example.fitnessway.util.Nutrient.filterNutrientsByType
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.NutrientGoalsFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileGoalsScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val goalsEditionFormState by viewModel.goalsEditionFormState.collectAsState()
    val isGoalsFormValid by viewModel.isGoalsFormValid.collectAsState()
    val nutrientsState = uiState.nutrientsState

    LaunchedEffect(nutrientsState) {
        if (nutrientsState is UiState.Success) {
            viewModel.initNutrientGoalsForm(
                goalsData = nutrientsState.data
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
                        onClick = viewModel::setGoalsThatChanged,
                        text = "Update",
                        enabled = isGoalsFormValid
                    )
                }
            )
        },

        content = {
            when (nutrientsState) {
                is UiState.Loading -> {
                    TextWithLoadingIndicator("Loading nutrients")
                }

                is UiState.Success -> {
                    goalsEditionFormState?.let { formState ->
                        val nutrientFields = remember(nutrientsState.data, formState) {
                            val fieldsProvider = NutrientGoalsFieldsProvider(
                                formState = formState,
                                onFieldUpdate = { fieldName, value ->
                                    viewModel.updateGoalEditionFormField(
                                        fieldName = fieldName,
                                        input = value
                                    )
                                }
                            )

                            NutrientType.entries.associateWith { type ->
                                val nutrientsByType = filterNutrientsByType(
                                    nutrients = nutrientsState.data,
                                    type = type
                                )

                                nutrientsByType.map { nutrientData ->
                                    fieldsProvider.nutrientGoal(nutrientData)
                                }
                            }
                        }

                        NutrientGoalsContent(nutrientFields = nutrientFields)
                    } ?: @Composable {
                        NotFoundText(text = "Form data not found")
                    }
                }

                else -> NotFoundText(text = "Something went wrong")
            }
        }
    )
}