package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.fitnessway.ui.shared.EditButton
import com.example.fitnessway.ui.theme.ImperialRed

@Composable
fun MoreOptionsPopup(
    isVisible: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 200)
        ) + scaleIn(
            initialScale = 0.8f,
            transformOrigin = TransformOrigin(1f, 0f),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 150)
        ) + scaleOut(
            targetScale = 0.8f,
            transformOrigin = TransformOrigin(1f, 0f),
            animationSpec = tween(durationMillis = 150)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(2f),
        content = {
            Box(
                contentAlignment = Alignment.CenterEnd,
                content = {
                    val shape = RoundedCornerShape(16.dp)

                    Box(
                        modifier = Modifier
                            .clip(shape)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = shape
                            )
                            .width(IntrinsicSize.Max)
                            .padding(16.dp),
                        content = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(
                                    16.dp
                                ),
                                content = {
                                    EditButton(
                                        onClick = onEdit,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    EditButton(
                                        onClick = onDelete,
                                        backgroundColor = ImperialRed,
                                        text = "Delete",
                                        modifier = Modifier.fillMaxWidth()
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