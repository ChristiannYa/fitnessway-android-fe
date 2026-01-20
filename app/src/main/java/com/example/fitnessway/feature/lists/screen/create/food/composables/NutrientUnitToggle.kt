package com.example.fitnessway.feature.lists.screen.create.food.composables

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.Animation

private const val OUTER_PADDING = 8
private const val INNER_PADDING = OUTER_PADDING / 2

private const val TOGGLE_BUTTON_WIDTH = 42
private const val TOGGLE_BUTTON_HEIGHT = 32

@Composable
fun NutrientUnitToggle(
    isInPercentagesMap: Boolean,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(OUTER_PADDING.dp),
        shape = CircleShape,
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) {
                MaterialTheme.colorScheme.outline
            } else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceTint)
        ) {
            val offsetX by animateFloatAsState(
                targetValue = if (isInPercentagesMap) 1f else 0f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                label = "NutrientUnitToggle_offsetX"
            )

            Box(
                modifier = Modifier
                    .zIndex(1f)
                    .padding(INNER_PADDING.dp)
                    .height(TOGGLE_BUTTON_HEIGHT.dp)
                    .width(TOGGLE_BUTTON_WIDTH.dp)
                    .offset {
                        IntOffset(
                            (offsetX * (TOGGLE_BUTTON_WIDTH + (INNER_PADDING / 2))).dp.roundToPx(),
                            0
                        )
                    }
                    .background(
                        color = if (enabled) {
                            if (isInPercentagesMap) MaterialTheme.colorScheme.onSurfaceVariant else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                            }
                        } else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
            )

            Row(
                modifier = Modifier
                    .zIndex(2f) // Row should be on top so text is visible
                    .clickable(
                        enabled = enabled,
                        onClick = { onToggle(!isInPercentagesMap) },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    )
                    .padding(INNER_PADDING.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                ToggleButton(
                    text = "",
                    isSelected = !isInPercentagesMap,
                    enabled = enabled
                )

                ToggleButton(
                    text = "%",
                    isSelected = isInPercentagesMap,
                    enabled = enabled
                )
            }
        }
    }
}

@Composable
private fun ToggleButton(
    text: String,
    isSelected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val textColor by animateColorAsState(
        targetValue = if (isSelected && enabled) {
            MaterialTheme.colorScheme.surfaceTint
        } else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = Animation.colorSpec,
        label = "NutrientUnitToggle_ToggleButton_textColor"
    )

    Box(
        modifier = modifier
            .height(TOGGLE_BUTTON_HEIGHT.dp)
            .width(TOGGLE_BUTTON_WIDTH.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NutrientUnitTogglePreview() {
    FitnesswayTheme {
        NutrientUnitToggle(
            isInPercentagesMap = false,
            enabled = true,
            onToggle = {}
        )
    }
}