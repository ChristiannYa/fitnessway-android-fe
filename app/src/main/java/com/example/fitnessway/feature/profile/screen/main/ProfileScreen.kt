package com.example.fitnessway.feature.profile.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.ImperialRed
import com.example.fitnessway.ui.theme.SilverMist
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun ProfileScreen(
    onSettings: () -> Unit,
    onGoals: () -> Unit,
) {
    Screen {
        Column {
            Text("This is the profile screen")

            TextButton(
                onClick = onSettings,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = SilverMist
                ),
                content = {
                    Text(
                        text = "Settings",
                        fontFamily = robotoSerifFamily,
                        fontWeight = FontWeight.Medium
                    )
                }
            )

            TextButton(
                onClick = onGoals,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = SilverMist
                ),
                content = {
                    Text(
                        text = "Goals",
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
fun ProfileScreenPreview() {
    FitnesswayTheme {
        ProfileScreen(
            onSettings = {},
            onGoals = {}
        )
    }
}
