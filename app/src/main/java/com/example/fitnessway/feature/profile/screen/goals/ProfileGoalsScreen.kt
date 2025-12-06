package com.example.fitnessway.feature.profile.screen.goals

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.profile.screen.goals.composables.NutrientGoalsContent
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessageAnimated
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.TextWithLoadingIndicator
import com.example.fitnessway.util.Constants
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
                title = "My Goals"
            )
        },

        content = {
            when (nutrientsState) {
                is UiState.Loading -> {
                    Log.d(Constants.DEBUG_TAG, "Loading nutrients")
                    TextWithLoadingIndicator("Loading nutrients")
                }

                is UiState.Success -> {
                    Log.d(Constants.DEBUG_TAG, "Loaded nutrients")
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
                        Log.d(Constants.DEBUG_TAG, "form data not found")
                        NotFoundText(text = "Form data not found")
                    }
                }

                else -> NotFoundText(text = "Something went wrong")
            }

            val nutrientsFetchErrMsg = (uiState.nutrientsState as? UiState.Error)?.message

            ApiErrorMessageAnimated(
                isVisible = nutrientsFetchErrMsg != null,
                errorMessage = nutrientsFetchErrMsg ?: ""
            )
        }
    )
}