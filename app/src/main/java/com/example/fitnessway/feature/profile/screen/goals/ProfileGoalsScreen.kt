package com.example.fitnessway.feature.profile.screen.goals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.SilverMist
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun ProfileGoalsScreen(
    onBackClick: () -> Unit,
) {
    Screen(
        header = {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onBackClick,
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
            }
        },

        content = {
            Column {
                Text("This is the goals screen")

                TextButton(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = SilverMist
                    ),
                    content = {
                        Text(
                            text = "Update Goals",
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