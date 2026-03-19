package com.example.fitnessway.feature.home.screen.foodselection.food_request

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodRequestScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                title = "Food Request"
            )
        }
    ) {
        Text("Food request screen")
    }
}