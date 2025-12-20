package com.example.fitnessway.feature.home.screen.logdetails.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodLogEditionField
import com.example.fitnessway.ui.shared.ActionButton
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer

@Composable
fun EditionMode(
    fields: List<FoodLogEditionField>,
    isDoneEnabled: Boolean,
    onDone: () -> Unit,
    onCancel: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier
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
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .areaContainer(
                    areaColor = MaterialTheme.colorScheme.primaryContainer
                ),
            content = {
                // Edition Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    content = {
                        ActionButton(
                            text = "Done",
                            onClick = onDone,
                            enabled = isDoneEnabled,
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
                        .areaContainer(
                            size = AreaContainerSize.LARGE,
                            areaColor = MaterialTheme.colorScheme.secondaryContainer
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
}