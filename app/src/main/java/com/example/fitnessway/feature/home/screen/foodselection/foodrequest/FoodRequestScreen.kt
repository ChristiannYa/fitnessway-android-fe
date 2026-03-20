package com.example.fitnessway.feature.home.screen.foodselection.foodrequest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.food.creation.composables.FoodCreationFormScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodRequestScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getNutrients()
    }

    val user = userFlow
    val foodRequestState = uiState.foodRequestState
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState

    val foodRequestErrorMessage = Ui.handleTempApiErrMsg(
        uiState = foodRequestState,
        onTimeOut = viewModel::resetFoodRequestState
    )

    if (user !== null) {
        FoodCreationFormScreen(
            onBackClick = onBackClick,
            foodCreation = viewModel,
            nutrientsUiState = nutrientsUiState,
            isUserPremium = user.isPremium,
            onResetSubmissionState = viewModel::resetFoodRequestState,
            submissionState = foodRequestState,
            submissionErrorMessage = foodRequestErrorMessage,
            onSubmit = viewModel::addFoodRequest
        )
    }
}