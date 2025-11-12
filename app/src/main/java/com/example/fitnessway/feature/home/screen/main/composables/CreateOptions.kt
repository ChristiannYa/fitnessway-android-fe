package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
) {
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
            .padding(12.dp)
            .clickable(
                onClick = onCreate
            ),
        contentAlignment = Alignment.Center,
        content = {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Create $optionName",
                modifier = Modifier.size(18.dp),
                tint = WhiteFont
            )
        }
    )
}
