package com.example.fitnessway.feature.lists.screen.main.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.TextWithLoadingIndicator
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.util.UiState

fun LazyListScope.foodsList(
    state: UiState<List<FoodInformation>>,
    onViewDetails: (FoodInformation) -> Unit
) {
    when (state) {
        is UiState.Loading -> item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
                content = {
                    TextWithLoadingIndicator("Loading Foods")
                }
            )
        }

        is UiState.Success -> {
            val foods = state.data

            if (foods.isEmpty()) {
                item {
                    NotFoundText("Foods that you add to your list will appear here")
                }
            } else {
                items(
                    items = foods,
                    key = { food -> food.information.id }
                ) { food ->
                    Food(
                        food = food,
                        onViewDetails = { onViewDetails(food) }
                    )
                }
            }
        }

        is UiState.Error -> item { ApiErrorMessage(state.message) }
        is UiState.Idle -> {}
    }
}

@Composable
private fun Food(
    food: FoodInformation,
    onViewDetails: () -> Unit,
) {
    val missingBrand = food.information.brand == null || food.information.brand == ""

    Column(
        modifier = Modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .areaContainer(
                size = AreaContainerSize.SMALL,
                onClick = onViewDetails,
                areaColor = MaterialTheme.colorScheme.primaryContainer
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            Text(
                text = food.information.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (missingBrand) "~" else food.information.brand,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}
