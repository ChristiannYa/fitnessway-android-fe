package com.example.fitnessway.feature.lists.screen.details

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailsScreen(
    listsViewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val selectedFood by listsViewModel.selectedFood.collectAsState()

    Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                title = "Food Details"
            )
        },
        content = {
            val food = selectedFood

            if (food == null) {
                Text("No food selected")
            } else {
                Column(
                    content = {
                        Text(text = "This is the details screen")
                        Text(text = "Food selected: ${food.information.name}")
                    }
                )
            }
        }
    )
}