package com.example.fitnessway.feature.home.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessway.feature.home.composables.DatePicker
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.SilverMist
import com.example.fitnessway.ui.theme.robotoSerifFamily
import androidx.compose.runtime.getValue

@Composable
fun HomeScreen(
    onFoodSelection: () -> Unit,
) {
    val viewModel: HomeViewModel = viewModel()
    val selectedDate by viewModel.selectedDate.collectAsState()

    Screen {
        Column {
            DatePicker(
                date = viewModel.getFormattedDay(selectedDate),
                goNextDay = { viewModel.changeDay(1) },
                goPrevDay = { viewModel.changeDay(-1) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onFoodSelection,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = SilverMist
                ),
                content = {
                    Text(
                        text = "Log",
                        fontFamily = robotoSerifFamily,
                        fontWeight = FontWeight.Medium
                    )
                }
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    FitnesswayTheme {
        HomeScreen(onFoodSelection = {})
    }
}