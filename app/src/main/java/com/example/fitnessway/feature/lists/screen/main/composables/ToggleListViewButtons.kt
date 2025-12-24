package com.example.fitnessway.feature.lists.screen.main.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fitnessway.R
import com.example.fitnessway.data.model.food.ListOption
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Animation.colorSpec

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
    val baseIconSize = 18.dp

    val (icon, optionName, activeColor) = when (option) {
        ListOption.Food -> Triple(
            R.drawable.food,
            "Food",
            MaterialTheme.colorScheme.primary
        )

        ListOption.Supplement -> Triple(
            R.drawable.energy,
            "Supplement",
            MaterialTheme.colorScheme.primary
        )
    }

    val iconSize by animateDpAsState(
        targetValue = if (isSelected) baseIconSize else baseIconSize - 6.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconSize"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface,
        animationSpec = colorSpec,
        label = "listOptionIconTint"
    )

    val areaColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.surfaceVariant
        } else MaterialTheme.colorScheme.primaryContainer,
        animationSpec = colorSpec,
        label = "listOptionAreaColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .areaContainer(
                size = AreaContainerSize.SMALL,
                onClick = onClick,
                showsIndication = false,
                areaColor = areaColor
            )
            .height(baseIconSize),
        contentAlignment = Alignment.Center,
        content = {
            Icon(
                painter = painterResource(icon),
                contentDescription = "View ${optionName}s",
                modifier = Modifier.size(iconSize),
                tint = iconTint
            )
        }
    )
}