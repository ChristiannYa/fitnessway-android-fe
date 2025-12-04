package com.example.fitnessway.feature.profile.screen.goals

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
            when (uiState.nutrientsState) {
                is UiState.Loading -> TextWithLoadingIndicator("Loading nutrients")

                is UiState.Success -> {
                    Text("Nutrients loaded")
                }

                else -> NotFoundText(text = "Something went wrong")
            }

            /**
             *  If `(uiState.nutrientsState as UiState.Error).message` were to be used it would
             *  throw an error upon composition. because `as UiState.Error` assumes that the state
             *  would always be error, when it can be one of: loading, success, or idle
             *
             *  `as?` ensures to safe cast to `UiState.Error`, and it would return null when its not
             *  `UiState.Error`
             *
             *  `?.message` safely accesses the message when its not null.
             *  This safe access is needed because if `(...).message` were to be used, whenever it
             *  gets a null value, it will throw a NullPointerException error
             */
            val nutrientsFetchErrMsg = (uiState.nutrientsState as? UiState.Error)?.message

            ApiErrorMessageAnimated(
                isVisible = nutrientsFetchErrMsg != null,
                errorMessage = nutrientsFetchErrMsg ?: ""
            )
        }
    )
}