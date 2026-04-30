package com.example.fitnessway.feature.lists.screen.request

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.fitnessway.data.mappers.toEdibleType
import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.edible.creation.composables.FoodCreationFormScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodRequestScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val listOption by viewModel.edibleListFilter.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getNutrients()
    }

    val user = userFlow
    val edibleRequestAddState = uiState.edibleRequestAddState.getValue(listOption.toEdibleType())
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState

    val edibleRequestErrorMessage = Ui.handleTempApiErrMsg(
        uiState = edibleRequestAddState,
        onTimeOut = viewModel::resetFoodRequestState
    )

    if (user != null) {
        FoodCreationFormScreen(
            onBackClick = onBackClick,
            edibleCreation = viewModel.requestManager,
            edibleSource = EdibleSource.APP,
            edibleType = listOption.toEdibleType(),
            nutrientsUiState = nutrientsUiState,
            isUserPremium = user.isPremium,
            onResetSubmissionState = viewModel::resetFoodRequestState,
            submissionState = edibleRequestAddState,
            submissionErrorMessage = edibleRequestErrorMessage,
            onSubmit = viewModel::addEdibleRequest
        )
    }
}