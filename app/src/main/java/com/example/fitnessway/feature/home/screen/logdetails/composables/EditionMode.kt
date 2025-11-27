package com.example.fitnessway.feature.home.screen.logdetails.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodLogEditionField
import com.example.fitnessway.ui.shared.ToggleEditButton
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge

@Composable
fun EditionMode(
    fields: List<FoodLogEditionField>,
    isDoneEnabled: Boolean,
    onDone: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize(),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    // Edition Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        content = {
                            val backgroundColor = if (isDoneEnabled) {
                                MaterialTheme.colorScheme.primary
                            } else MaterialTheme.colorScheme.surfaceVariant

                            ToggleEditButton(
                                text = "Done",
                                onClick = onDone,
                                enabled = isDoneEnabled,
                                backgroundColor = backgroundColor
                            )

                            ToggleEditButton(
                                text = "Cancel",
                                onClick = onCancel,
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    )

                    // Fields
                    Box(
                        modifier = Modifier
                            .areaContainerLarge(
                                areaColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                        content = {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                content = {
                                    fields.forEach { field ->
                                        item {
                                            EditableField(field = field)
                                        }
                                    }
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}