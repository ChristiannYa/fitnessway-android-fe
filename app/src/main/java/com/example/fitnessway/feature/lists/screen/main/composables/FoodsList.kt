package com.example.fitnessway.feature.lists.screen.main.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.ui.shared.Loading.LoadingArea
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Formatters.formatUiErrorMessage
import com.example.fitnessway.util.UiState

fun LazyListScope.foodsList(
    state: UiState<List<FoodInformation>>,
    onViewDetails: (FoodInformation) -> Unit
) {
    when (state) {
        is UiState.Loading -> item { LoadingArea("Loading foods") }

        is UiState.Success -> {
            val foods = state.data

            if (foods.isEmpty()) {
                item {
                    NotFoundMessage("Foods that you add to your list will appear here")
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

        else -> {}
    }

    item("foodListErrorBanner") {
        NotFoundMessageAnimated(
            isVisible = state is UiState.Error,
            message = formatUiErrorMessage(state)
        )
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
