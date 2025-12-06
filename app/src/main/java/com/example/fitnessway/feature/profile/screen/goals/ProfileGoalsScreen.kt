package com.example.fitnessway.feature.profile.screen.goals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.fitnessway.feature.profile.screen.goals.composables.NutrientGoalsContent
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessageAnimated
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.TextWithLoadingIndicator
import com.example.fitnessway.util.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileGoalsScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val nutrientsState = uiState.nutrientsState
    val nutrientFields by viewModel.nutrientFields.collectAsState()

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
                    TextWithLoadingIndicator("Loading nutrients")
                }

                is UiState.Success -> {
                    nutrientFields?.let { fields ->
                        NutrientGoalsContent(nutrientFields = fields)
                    } ?: TextWithLoadingIndicator("Preparing form")
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