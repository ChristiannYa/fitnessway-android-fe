package com.example.fitnessway.feature.home.screen.foodselection.foodlog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.home.screen.foodselection.foodlog.composables.EditionButtons
import com.example.fitnessway.feature.home.screen.foodselection.foodlog.composables.FoodLogInformationList
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodLogFieldsProvider
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodLogScreen(
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val foodLogFormState by viewModel.foodLogFormState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val foodLogAddState = uiState.foodLogAddState

    val foodCategory by viewModel.foodLogCategory.collectAsState()
    val selectedFoodToLog by viewModel.selectedFoodToLog.collectAsState()
    val time = viewModel.getCurrentTime()

    var shouldShowFoodLogSuccess by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetFoodLogAddState()
        }
    }

    LaunchedEffect(selectedFoodToLog) {
        selectedFoodToLog?.let { food ->
            viewModel.initializeFoodLogForm(food, time)
        }
    }

    LaunchedEffect(foodLogAddState) {
        if (foodLogAddState is UiState.Success) {
            shouldShowFoodLogSuccess = true
            delay(5000)
            shouldShowFoodLogSuccess = false
            viewModel.resetFoodLogAddState()
        }
    }

    Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                title = "Log Information"
            )
        },
        content = {
            val food = selectedFoodToLog

            if (food == null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Food information not found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            } else {
                foodLogFormState?.let { formState ->
                    val fieldsProvider = FoodLogFieldsProvider(
                        formState = formState,
                        onFieldUpdate = { fieldName, value ->
                            viewModel.updateFoodLogFormField(fieldName, value)
                        }
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        content = {
                            EditionButtons(
                                isValid = viewModel.isFoodLogFormValid,
                                isEditing = formState.isEditing,
                                isSubmitSuccess = shouldShowFoodLogSuccess,
                                onEdit = { viewModel.startFormEdit(formState.data) },
                                onSave = { viewModel.saveFormEdit(formState.data) },
                                onCancel = { viewModel.cancelFormEdit(formState.data) },
                                onSubmit = { viewModel.addFoodLog() },

                                onSubmitText = "Log"
                            )

                            if (foodLogAddState is UiState.Error) {
                                ApiErrorMessage(foodLogAddState.message)
                            }

                            FoodLogInformationList(
                                food = food,
                                category = foodCategory,
                                fieldsProvider = fieldsProvider,
                                isEditing = formState.isEditing
                            )
                        }
                    )
                }
            }
        }
    )
}

