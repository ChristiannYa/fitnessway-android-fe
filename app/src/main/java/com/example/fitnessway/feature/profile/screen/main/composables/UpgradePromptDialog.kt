package com.example.fitnessway.feature.profile.screen.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.shared.PremiumIcon

@Composable
fun UpgradePromptDialog(
    onDismiss: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Upgrade to Premium",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        content = {
                            Text(
                                text = "You're currently on a Free account",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Upgrade to Premium and enjoy:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp),
                        content = {
                            PremiumBenefit("Unlimited nutrient tracking")
                            PremiumBenefit("Custom color themes")
                            PremiumBenefit("Advanced analytics")
                        }
                    )
                }
            )
        },
        confirmButton = {
            Button(
                onClick = onUpgradeClick,
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Text("Upgrade Now")
                }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Text("Maybe Later")
                }
            )
        }
    )
}

@Composable
private fun PremiumBenefit(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            PremiumIcon()

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}