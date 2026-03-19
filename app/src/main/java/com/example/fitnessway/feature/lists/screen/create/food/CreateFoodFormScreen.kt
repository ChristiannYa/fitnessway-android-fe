package com.example.fitnessway.feature.lists.screen.create.food

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.util.Ui.handleTempApiErrorMessage
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.food.creation.FoodCreationFormScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateFoodFormScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()

    val user = userFlow
    val foodAddState = uiState.foodAddState
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState

    val foodAddErrMsg = handleTempApiErrorMessage(
        uiState = foodAddState,
        onTimeOut = viewModel::resetFoodAddState
    )

    if (user != null && nutrientsUiState is UiState.Success) {
        FoodCreationFormScreen(
            onBackClick = onBackClick,
            foodCreation = viewModel,
            nutrients = nutrientsUiState.data,
            isUserPremium = user.isPremium,
            onResetSubmissionState = viewModel::resetFoodAddState,
            submissionState = uiState.foodAddState,
            submissionErrorMessage = foodAddErrMsg,
            onSubmit = viewModel::addFood
        )
    }
}