package com.example.fitnessway.feature.lists.screen.request

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.edible.creation.composables.FoodCreationFormScreen
import com.example.fitnessway.util.extensions.getEdibleType
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodRequestScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val listOption by viewModel.listOptionFilter.collectAsState()

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

    if (user != null) {
        FoodCreationFormScreen(
            onBackClick = onBackClick,
            edibleCreation = viewModel.requestManager,
            edibleSource = EdibleSource.APP,
            edibleType = listOption.getEdibleType(),
            nutrientsUiState = nutrientsUiState,
            isUserPremium = user.isPremium,
            onResetSubmissionState = viewModel::resetFoodRequestState,
            submissionState = foodRequestState,
            submissionErrorMessage = foodRequestErrorMessage,
            onSubmit = viewModel::addFoodRequest
        )
    }
}