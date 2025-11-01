package com.example.fitnessway.feature.home.screen.main.foodselection.foodlog

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.Formatters.doubleFormatter
import org.koin.compose.viewmodel.koinViewModel

data class Label(
    val label: String,
    val value: String
)

@Composable
fun FoodLogScreen(
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val foodCategory by viewModel.foodLogCategory.collectAsState()
    val selectedFood by viewModel.selectedFood.collectAsState()
    val time = viewModel.currentTime

    Screen(
        header = {
            Header(
                onBackClick,
                title = "Log Information"
            )
        },
        content = {
            // @NOTE
            // We create a local `food` variable because if `selectedFood` were to be used instead
            // we would get error, "Smart cast to 'FoodInformation' is impossible, because
            // 'selectedFood' is a delegated property."
            //
            // This is because `selectedFood` is a delegated property that could change between
            // the null check and when we access its properties, since the underlying StateFlow
            // it delegates to can emit new values.
            //
            // By creating the local immutable variable, the compiler can safely smart cast it
            // after the null check, knowing it cannot change during the function execution
            val food = selectedFood

            if (food == null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Food not found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            } else {
                val category = foodCategory.replaceFirstChar { it.uppercase() }
                val servings = doubleFormatter(food.information.amountPerServing)

                LogInformationList(
                    category = category,
                    foodName = food.information.name,
                    servings = servings,
                    time = time
                )
            }
        }
    )
}

@Composable
fun LogInformationList(
    category: String,
    foodName: String,
    servings: String,
    time: String
) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val spacing = 8.dp

    val logInformation = listOf(
        Label(category, foodName),
        Label("Servings", servings),
        Label("Time", time)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind() {
                val borderWidth = 1.dp.toPx()

                // Top border
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = borderWidth
                )

                // Bottom border
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = borderWidth
                )
            }
            .padding(vertical = spacing * 2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {
            logInformation.forEachIndexed { index, item ->
                LogInformation(item)

                if (index < logInformation.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = spacing),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    )
}

@Composable
fun LogInformation(item: Label) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        content = {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = item.value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LogInformationListPreview() {
    FitnesswayTheme {
        LogInformationList(
            category = "Breakfast",
            foodName = "Grilled Chicken",
            servings = "2.7",
            time = "07:37"
        )
    }
}
