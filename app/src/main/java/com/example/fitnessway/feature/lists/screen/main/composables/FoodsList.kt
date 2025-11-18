package com.example.fitnessway.feature.lists.screen.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerSmall
import com.example.fitnessway.util.UiState

@Composable
fun FoodsList(
    state: UiState<List<FoodInformation>>,
    onViewDetails: (FoodInformation) -> Unit
) {
    when (state) {
        is UiState.Loading -> Text("Loading foods")
        is UiState.Success -> Foods(
            foods = state.data,
            onViewDetails = onViewDetails
        )

        is UiState.Error -> ApiErrorMessage(state.message)
        is UiState.Idle -> {}
    }
}

@Composable
fun Foods(
    foods: List<FoodInformation>,
    onViewDetails: (FoodInformation) -> Unit
) {
    if (foods.isEmpty()) {
        Text(
            text = "Foods that you add to your list will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                foods.forEach { food ->
                    item {
                        Food(
                            food = food,
                            onViewDetails = { onViewDetails(food) }
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun Food(
    food: FoodInformation,
    onViewDetails: () -> Unit,
) {
    val missingBrand = food.information.brand == null || food.information.brand == ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .areaContainerSmall(
                onClick = onViewDetails
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
