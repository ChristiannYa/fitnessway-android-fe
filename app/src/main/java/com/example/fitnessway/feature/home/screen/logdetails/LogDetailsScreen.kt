package com.example.fitnessway.feature.home.screen.logdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.home.screen.logdetails.composables.LogDetails
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.robotoSerifFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun LogDetailsScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val selectedFoodLog by viewModel.selectedFoodLog.collectAsState()

    Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                title = "Food Log Details",
                extraContent = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .clickable(
                                onClick = {}
                            )
                            .padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                        content = {
                            Text(
                                text = "Edit",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                fontFamily = robotoSerifFamily
                            )
                        }
                    )
                }
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
                LogDetails(foodLog)
            }
        }
    )
}
