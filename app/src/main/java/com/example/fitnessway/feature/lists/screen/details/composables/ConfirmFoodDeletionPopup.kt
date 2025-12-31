package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.ImperialRed
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Animation

@Composable
fun ConfirmFoodDeletionPopup(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = Animation.ComposableTransition.PopUpV1.enter,
        exit = Animation.ComposableTransition.PopUpV1.exit,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .width(260.dp)
                .areaContainer(
                    areaColor = MaterialTheme.colorScheme.surfaceVariant
                )
        ) {
            val space = 14.dp

            Column(verticalArrangement = Arrangement.spacedBy(space)) {
                Text(
                    text = "Are you sure you want to delete this food?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(space),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FoodDeletionConfirmButton(
                        onClick = onCancel,
                        text = "No",
                        backgroundColor = MaterialTheme.colorScheme.surfaceTint,
                        modifier = Modifier.weight(1f)
                    )

                    FoodDeletionConfirmButton(
                        onClick = onConfirm,
                        text = "Yes",
                        backgroundColor = ImperialRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FoodDeletionConfirmButton(
    onClick: () -> Unit,
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(shape)
            .background(
                color = backgroundColor,
                shape = shape
            )
            .clickable(
                onClick = onClick
            )
            .padding(vertical = 12.dp),
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = WhiteFont,
                fontFamily = robotoSerifFamily
            )
        }
    )
}