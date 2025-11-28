package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun FoodMoreOptionsButton(
    onClick: () -> Unit,
) {
    Box(
        content = {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        onClick = onClick
                    ),
                content = {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = null
                    )
                }
            )
        }
    )
}