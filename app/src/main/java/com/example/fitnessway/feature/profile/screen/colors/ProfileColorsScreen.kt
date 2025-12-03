package com.example.fitnessway.feature.profile.screen.colors

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.SilverMist
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun ProfileColorsScreen(
    onBackClick: () -> Unit
) {
    Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                title = "Color Palette"
            )
        },

        content = {
            Column {
                TextButton(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = SilverMist
                    ),
                    content = {
                        Text(
                            text = "Update colors",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = robotoSerifFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }
                )
            }
        }
    )
}