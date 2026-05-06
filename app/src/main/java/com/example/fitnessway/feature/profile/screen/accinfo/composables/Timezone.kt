package com.example.fitnessway.feature.profile.screen.accinfo.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.fitnessway.constants.timezones
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer

@Composable
fun Timezone(
    userTimezone: String,
    isRequestLoading: Boolean,
    onSet: (String) -> Unit,
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    var selectedTimezone by remember { mutableStateOf(userTimezone) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.areaContainer()
    ) {
        Text("Timezone")

        Text(
            text = userTimezone,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(
                    onClick = { isDialogVisible = true }
                )
        )
    }

    if (isDialogVisible) {
        AlertDialog(
            title = {
                Column {
                    Text("Select a Timezone")

                    Text(
                        text = selectedTimezone,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                            .padding(bottom = 2.dp)
                    )
                }
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(timezones) { timezone ->
                        val shape = RoundedCornerShape(size = 8.dp)
                        val isSelected = selectedTimezone == timezone

                        Text(
                            text = timezone,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.surfaceVariant
                            } else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shape)
                                .background(
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    } else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = shape
                                )
                                .clickable(
                                    onClick = { selectedTimezone = timezone },
                                    interactionSource = null,
                                    indication = null
                                )
                                .padding(
                                    horizontal = if (isSelected) 6.dp else 0.dp,
                                    vertical = 10.dp
                                )
                        )
                    }
                }
            },
            onDismissRequest = {
                if (isRequestLoading) return@AlertDialog

                isDialogVisible = false
                selectedTimezone = userTimezone
            },
            confirmButton = {
                Clickables.DoneButton(
                    enabled = !isRequestLoading,
                    isLoading = isRequestLoading,
                    onClick = { onSet(selectedTimezone) }
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}