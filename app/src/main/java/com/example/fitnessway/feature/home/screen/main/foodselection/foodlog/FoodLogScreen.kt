package com.example.fitnessway.feature.home.screen.main.foodselection.foodlog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.home.screen.main.foodselection.foodlog.composables.FoodLogInformationList
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import org.koin.compose.viewmodel.koinViewModel

data class Label(
    val label: String,
    val value: String
)

@Composable
fun FoodLogScreen(
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val foodCategory by viewModel.foodLogCategory.collectAsState()
    val selectedFood by viewModel.selectedFood.collectAsState()
    val time = viewModel.getCurrentTime()

    val foodLogFormState by viewModel.foodLogFormState.collectAsState()

    LaunchedEffect(selectedFood) {
        selectedFood?.let { food ->
            viewModel.initializeFoodLogForm(food, time)
        }
    }

    foodLogFormState?.let { formState ->

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
                val category = foodCategory.replaceFirstChar { it.uppercase() }

                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    content = {
                        // EditionButtons()

                        FoodLogInformationList(
                            category = category,
                            foodName = food.information.name,
                            amountPerServing = food.information.amountPerServing,
                            servingUnit = food.information.servingUnit,
                            time = time
                        )
                    }
                )
            }
        }
    )
}

