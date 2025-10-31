package com.example.fitnessway.feature.home.screen.foodselection

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.Constants
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodSelectionScreen(
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val foodCategory by viewModel.foodLogCategory.collectAsState()

    Log.d(Constants.DEBUG_TAG, "FoodSelectionScreen received food category: $foodCategory")

    Screen(
        header = {
            Header(
                onBackClick,
                "${foodCategory.replaceFirstChar { it.uppercase() }} selection"
            )
        },
        content = {
            Column {
                Text("This is the food selection screen")
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