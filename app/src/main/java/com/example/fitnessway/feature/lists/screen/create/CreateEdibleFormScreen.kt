package com.example.fitnessway.feature.lists.screen.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.fitnessway.data.mappers.toEdibleType
import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.util.Ui.handleTempApiErrMsg
import com.example.fitnessway.util.edible.creation.composables.FoodCreationFormScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateFoodFormScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val listOption by viewModel.edibleListFilter.collectAsState()

    val user = userFlow
    val foodAddState = uiState.foodAddState
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState

    val foodAddErrMsg = handleTempApiErrMsg(
        uiState = foodAddState,
        onTimeOut = viewModel::resetFoodAddState
    )

    if (user != null) {
        FoodCreationFormScreen(
            onBackClick = onBackClick,
            edibleCreation = viewModel.creationManager,
            edibleSource = EdibleSource.USER,
            edibleType = listOption.toEdibleType(),
            nutrientsUiState = nutrientsUiState,
            isUserPremium = user.isPremium,
            onResetSubmissionState = viewModel::resetFoodAddState,
            submissionState = uiState.foodAddState,
            submissionErrorMessage = foodAddErrMsg,
            onSubmit = viewModel::addEdible
        )
    }
}