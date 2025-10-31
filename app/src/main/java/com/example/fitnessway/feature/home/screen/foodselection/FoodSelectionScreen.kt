package com.example.fitnessway.feature.home.screen.foodselection

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitnessway.feature.home.screen.foodselection.composables.Foods
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodSelectionScreen(
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val foodCategory by viewModel.foodLogCategory.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getFoods()
    }

    Screen(
        header = {
            Header(
                onBackClick,
                "${foodCategory.replaceFirstChar { it.uppercase() }} selection"
            )
        },
        content = {
            Column {
                Foods(uiState.foodsState)
            }
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FoodSelectionPreview() {
    FitnesswayTheme {
        FoodSelectionScreen(onBackClick = {})
    }
}