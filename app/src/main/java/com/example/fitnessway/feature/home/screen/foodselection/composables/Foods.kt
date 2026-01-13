package com.example.fitnessway.feature.home.screen.foodselection.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.MUser.Model.User
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Formatters.formatUiErrorMessage
import com.example.fitnessway.util.UFood
import com.example.fitnessway.util.UNutrient.getColor
import com.example.fitnessway.util.UiState

@Composable
fun Foods(
    state: UiState<List<FoodInformation>>,
    onFoodClick: (FoodInformation) -> Unit,
    user: User,
    modifier: Modifier = Modifier
) {
    when (state) {
        is UiState.Success -> Foods(
            foods = state.data,
            onFoodClick = { onFoodClick(it) },
            user = user,
            modifier = modifier
        )

        is UiState.Idle -> {}
        else -> {}
    }

    NotFoundMessageAnimated(
        isVisible = state is UiState.Error,
        message = formatUiErrorMessage(state)
    )
}

@Composable
private fun Foods(
    foods: List<FoodInformation>,
    onFoodClick: (FoodInformation) -> Unit,
    user: User,
    modifier: Modifier = Modifier
) {
    if (foods.isEmpty()) {
        NotFoundMessage("Foods that you add to your list will appear here")
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier,
            content = {
                foods.forEach { food ->
                    item {
                        Food(
                            food = food,
                            onFoodClick = { onFoodClick(food) },
                            user = user
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun Food(
    food: FoodInformation,
    onFoodClick: () -> Unit,
    user: User
) {
    val foodBrandText = UFood.Ui.getFoodBrandText(food.information.brand)
    val foodBrandColor = UFood.Ui.getFoodBrandColor()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .areaContainer(
                size = AreaContainerSize.SMALL,
                onClick = onFoodClick

            ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            Text(
                text = food.information.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = foodBrandText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = foodBrandColor
            )

            if (user.isPremium) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    content = {
                        food.nutrients.basic.forEach { (nutrientData, amount) ->
                            val nutrientColor = getColor(nutrientData.preferences.hexColor)

                            if (nutrientColor != null) {
                                Text(
                                    text = doubleFormatter(amount),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = nutrientColor
                                )
                            }
                        }
                    }
                )
            }
        }
    )
}
