package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fitnessway.R
import com.example.fitnessway.ui.theme.WhiteFont

enum class CreateOption {
    Food,
    Supplement,
}


@Composable
fun CreateOptions(
    onCreateFood: () -> Unit,
    onCreateSupplement: () -> Unit,
    isVisible: Boolean
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it }, // Slide to top
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + fadeOut(
            animationSpec = tween(durationMillis = 300)
        ),
        content = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    Option(
                        createOption = CreateOption.Food,
                        onCreate = onCreateFood,
                        modifier = Modifier.weight(1f)
                    )
                    Option(
                        createOption = CreateOption.Supplement,
                        onCreate = onCreateSupplement,
                        modifier = Modifier.weight(1f)
                    )
                }
            )
        }
    )
}

@Composable
fun Option(
    createOption: CreateOption,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (createOption) {
        CreateOption.Food -> R.drawable.food
        CreateOption.Supplement -> R.drawable.energy
    }

    val optionName = when (createOption) {
        CreateOption.Food -> CreateOption.Food.name.replaceFirstChar { it.uppercase() }
        CreateOption.Supplement -> CreateOption.Supplement.name.replaceFirstChar { it.uppercase() }
    }

    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .padding(10.dp)
            .clickable(
                onClick = onCreate
            ),
        contentAlignment = Alignment.Center,
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                content = {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "Create $optionName",
                        modifier = Modifier.size(16.dp),
                        tint = WhiteFont
                    )

                    Text(
                        text = optionName,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    )
}
