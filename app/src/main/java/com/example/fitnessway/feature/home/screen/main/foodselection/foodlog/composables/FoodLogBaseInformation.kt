package com.example.fitnessway.feature.home.screen.main.foodselection.foodlog.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.home.screen.main.foodselection.foodlog.Label
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerSmall
import com.example.fitnessway.ui.theme.FitnesswayTheme

@Composable
fun FoodLogInformationList(
    category: String,
    foodName: String,
    amountPerServing: Double,
    servingUnit: String,
    time: String
) {
    val spacing = 8.dp

    val foodLogInformation = listOf(
        Label(category, foodName),
        Label("Servings", "1"),
        Label("Amount per serving ($servingUnit)", "$amountPerServing"),
        Label("Time", time)
    )

    Column(
        modifier = Modifier.areaContainerSmall(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {
            foodLogInformation.forEachIndexed { index, item ->
                FoodLogInformation(item)

                if (index < foodLogInformation.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = spacing),
                        color = MaterialTheme.colorScheme.onBackground.copy(0.1f)
                    )
                }
            }
        }
    )
}

@Composable
fun FoodLogInformation(item: Label) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = item.value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LogInformationListPreview() {
    FitnesswayTheme {
        FoodLogInformationList(
            category = "Breakfast",
            foodName = "Grilled Chicken",
            amountPerServing = 182.0,
            servingUnit = "g",
            time = "07:37"
        )
    }
}