package com.example.fitnessway.feature.home.screen.foodselection

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitnessway.feature.home.screen.foodselection.composables.Foods
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.Food.Ui.getFoodLogCategory
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodSelectionScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onSelectedFoodToLog: () -> Unit
) {
    val foodUiState by viewModel.foodRepoUiState.collectAsState()
    val foodLogCategory by viewModel.foodLogCategory.collectAsState()

    val user = viewModel.user

    LaunchedEffect(Unit) {
        viewModel.getFoods()
    }

    if (user != null) {
        // Create local copy of delegated category from the view model
        // If the following were to be done instead:
        //
        // ```
        // if (foodLogCategory != null) {
        //    val bar = getFoodLogCategory(foodLogCategory)
        // }
        // ```
        //
        // The error "Argument type mismatch: actual type is 'FoodLogCategories?', but
        // 'FoodLogCategories' was expected." would've been shown
        val foodLogCategoryCopy = foodLogCategory

        if (foodLogCategoryCopy != null) {
            val categoryString = getFoodLogCategory(foodLogCategoryCopy)

            Screen(
                header = {
                    Header(
                        onBackClick = onBackClick,
                        title = "$categoryString selection"
                    )
                }
            ) {
                Foods(
                    state = foodUiState.foodsUiState,
                    setSelectedFoodToLog = viewModel::setSelectedFoodToLog,
                    onSelectedFoodToLog,
                    user = user
                )
            }

        } else NotFoundText("Food log category not found")

    } else NotFoundText("Data not found")
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FoodSelectionPreview() {
    FitnesswayTheme {
        FoodSelectionScreen(
            onBackClick = {},
            onSelectedFoodToLog = {}
        )
    }
}