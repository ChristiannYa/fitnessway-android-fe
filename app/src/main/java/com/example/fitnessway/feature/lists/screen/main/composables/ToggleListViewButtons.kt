package com.example.fitnessway.feature.lists.screen.main.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fitnessway.R
import com.example.fitnessway.data.model.food.ListOption
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerSmall

@Composable
fun ToggleListViewButtons(
    selectedOption: ListOption,
    onToggleSelectedList: (ListOption) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            ListOptionButton(
                option = ListOption.Food,
                isSelected = selectedOption == ListOption.Food,
                onClick = { onToggleSelectedList(ListOption.Food) },
                modifier = Modifier.weight(1f)
            )
            ListOptionButton(
                option = ListOption.Supplement,
                isSelected = selectedOption == ListOption.Supplement,
                onClick = { onToggleSelectedList(ListOption.Supplement) },
                modifier = Modifier.weight(1f)
            )
        }
    )
}

@Composable
fun ListOptionButton(
    option: ListOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, optionName, activeColor) = when (option) {
        ListOption.Food -> Triple(
            R.drawable.food,
            "Food",
            Color(0xFFE53935)
        )
        ListOption.Supplement -> Triple(
            R.drawable.energy,
            "Supplement",
            Color(0xFFFDD835)
        )
    }

    val iconSize by animateDpAsState(
        targetValue = if (isSelected) 18.dp else 12.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconSize"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface.copy(0.5f),
        animationSpec = tween(durationMillis = 300),
        label = "listOptionIconTint"
    )

    val textTint by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurface.copy(0.5f)
        },
        animationSpec = tween(durationMillis = 300),
        label = "listOptionTextTint"
    )

    val areaColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.primaryContainer.copy(0.5f)
        },
        animationSpec = tween(durationMillis = 300),
        label = "listOptionAreaColor"
    )

    Box(
        modifier = modifier
            .areaContainerSmall(
                onClick = onClick,
                showsIndication = false,
                areaColor = areaColor
            ),
        contentAlignment = Alignment.Center,
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                content = {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "View ${optionName}s",
                        modifier = Modifier.size(iconSize),
                        tint = iconTint
                    )

                    Text(
                        text = optionName,
                        style = MaterialTheme.typography.labelSmall,
                        color = textTint
                    )
                }
            )
        }
    )
}