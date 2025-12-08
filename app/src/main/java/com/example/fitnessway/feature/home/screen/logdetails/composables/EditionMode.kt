package com.example.fitnessway.feature.home.screen.logdetails.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodLogEditionField
import com.example.fitnessway.ui.shared.ActionButton
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge
import com.example.fitnessway.util.Animation.colorSpec

@Composable
fun EditionMode(
    fields: List<FoodLogEditionField>,
    isDoneEnabled: Boolean,
    onDone: () -> Unit,
    onCancel: () -> Unit,
    isVisible: Boolean
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300)
        ),
        content = {
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
                                    val backgroundColor by animateColorAsState(
                                        targetValue = if (isDoneEnabled) {
                                            MaterialTheme.colorScheme.primary
                                        } else MaterialTheme.colorScheme.surfaceVariant,
                                        animationSpec = colorSpec,
                                        label = "logDetailsDoneButtonBackground"
                                    )

                                    ActionButton(
                                        text = "Done",
                                        onClick = onDone,
                                        enabled = isDoneEnabled,
                                        backgroundColor = backgroundColor
                                    )

                                    ActionButton(
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
    )
}