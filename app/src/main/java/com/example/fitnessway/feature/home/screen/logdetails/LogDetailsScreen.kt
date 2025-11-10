package com.example.fitnessway.feature.home.screen.logdetails

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun LogDetailsScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val selectedFoodLog by viewModel.selectedFoodLog.collectAsState()

    Screen(
        header = {
            val foodLog = selectedFoodLog
            val headerTitle = foodLog?.food?.information?.name ?: "Food not found"

            Header(
                onBackClick = onBackClick,
                title = headerTitle
            )
        },
        content = {
            val foodLog = selectedFoodLog

            if (foodLog == null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Food log information not found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Food log information",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}