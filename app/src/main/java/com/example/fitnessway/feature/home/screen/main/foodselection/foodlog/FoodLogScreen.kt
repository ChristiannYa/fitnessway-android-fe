package com.example.fitnessway.feature.home.screen.main.foodselection.foodlog

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
import com.example.fitnessway.feature.home.screen.main.foodselection.foodlog.composables.EditionButtons
import com.example.fitnessway.feature.home.screen.main.foodselection.foodlog.composables.FoodLogInformationList
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
    val selectedFood by viewModel.selectedFood.collectAsState()
    val time = viewModel.getCurrentTime()

    var shouldShowFoodLogSuccess by remember { mutableStateOf(false) }

    // This will run just in case the user exits the screen before the 5s after
    // the success state
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetFoodLogAddState()
        }
    }

    LaunchedEffect(selectedFood) {
        selectedFood?.let { food ->
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
                onBackClick,
                title = "Log Information"
            )
        },
        content = {
            // @NOTE
            // We create a local `food` variable because if `selectedFood` were to be used instead
            // we would get error, "Smart cast to 'FoodInformation' is impossible, because
            // 'selectedFood' is a delegated property."
            //
            // This is because `selectedFood` is a delegated property that could change between
            // the null check and when we access its properties, since the underlying StateFlow
            // it delegates to can emit new values.
            //
            // By creating the local immutable variable, the compiler can safely smart cast it
            // after the null check, knowing it cannot change during the function execution
            val food = selectedFood

            if (food == null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Food not found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            } else {
                // @NOTE
                // `foodLogFormState` is set as null by default. It only gets initialized after the
                // composable runs for the first time (inside LaunchedEffect). Once initialized it
                // causes a recomposition, making `foodLogFormState` available
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

                                // Just reading state, so there is no need to use the view model to
                                // obtain the value
                                isEditing = formState.isEditing,

                                isSubmitSuccess = shouldShowFoodLogSuccess,

                                // Updating state...
                                onEdit = { viewModel.startFoodLogEdit() },
                                onSave = { viewModel.saveFoodLogEdit() },
                                onCancel = { viewModel.cancelFoodLogEdit() },
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

