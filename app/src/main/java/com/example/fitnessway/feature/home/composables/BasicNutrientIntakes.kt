package com.example.fitnessway.feature.home.composables

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.data.model.nutrient.NutrientIntake
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.calcNutrientData
import com.example.fitnessway.util.filterDisplayedNutrients

@Composable
fun BasicNutrientIntakes(state: UiState<NutrientsByType>, user: User) {
    when (state) {
        is UiState.Loading -> Text("Loading basic nutrient intakes")
        is UiState.Success -> BasicNutrients(state.data, user)
        is UiState.Error -> ApiErrorMessage(state.message)
        UiState.Idle -> {}
    }
}

@Composable
fun BasicNutrients(nutrients: NutrientsByType, user: User) {
    Row(
        modifier = Modifier.areaContainer(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            val displayedNutrients = filterDisplayedNutrients(
                nutrients.basic,
                user
            )

            if (displayedNutrients.isEmpty()) {
                Text(
                    text = "Set basic nutrient goals in order to see your intake progress",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                displayedNutrients.forEach { nutrient ->
                    BasicNutrient(nutrient)
                }
            }
        }
    )
}

@Composable
fun BasicNutrient(intake: NutrientIntake) {
    val nutrientData = remember(intake) { calcNutrientData(intake) }

    val barRadius = 16.dp
    val spacedBy = 12.dp

    val progressHeight = doubleFormatter(nutrientData.progress / 100f).toFloat()
    val goalDisplay = if (intake.goal != null) doubleFormatter(intake.goal) else "0"
    val intakeDisplay = doubleFormatter(intake.intake)

    Column(
        modifier = Modifier
            .width(76.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacedBy),
        content = {
            Text(
                text = goalDisplay,
                style = MaterialTheme.typography.bodyMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(156.dp)
                    .background(
                        color = MaterialTheme.colorScheme.inverseSurface.copy(
                            0.03f
                        ),
                        shape = RoundedCornerShape(barRadius),
                    )
                    .clip(RoundedCornerShape(barRadius)),
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(progressHeight)
                            .background(
                                color = MaterialTheme.colorScheme.inverseSurface,
                                shape = RoundedCornerShape(
                                    bottomStart = barRadius,
                                    bottomEnd = barRadius,
                                )
                            )
                            .align(Alignment.BottomCenter)
                    )
                    Text(
                        text = intakeDisplay,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = -(spacedBy * 2))
                    )
                }
            )

            Text(
                text = intake.nutrient.name,
                color = MaterialTheme.colorScheme.inverseSurface,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NutrientsPreview() {
    val sampleNutrientsByType = NutrientsByType(
        basic = listOf(sampleCalories, sampleProtein, sampleCarbs),
        vitamin = emptyList(),
        mineral = emptyList(),
    )

    FitnesswayTheme {
        BasicNutrients(sampleNutrientsByType, sampleUser)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NutrientPreview() {
    FitnesswayTheme {
        BasicNutrient(sampleProtein)
    }
}

val sampleUser = User(
    id = "117",
    name = "Christian",
    email = "chris.lopez.webdev@gmail.com",
    isPremium = false,
    createdAt = "04-24-2002",
    updatedAt = "10-28-2025"
)

val calories = Nutrient(
    id = 1,
    name = "Calories",
    symbol = "Kcal",
    unit = "kcal",
    type = NutrientType.BASIC,
    isPremium = false
)

val sampleCalories = NutrientIntake(
    nutrient = calories,
    goal = 2890.0,
    intake = 1854.9
)

val protein = Nutrient(
    id = 2,
    name = "Protein",
    symbol = "P",
    unit = "g",
    type = NutrientType.BASIC,
    isPremium = false
)

val sampleProtein = NutrientIntake(
    nutrient = protein,
    goal = 90.0,
    intake = 42.6,
)

val carbs = Nutrient(
    id = 3,
    name = "Carbs",
    symbol = "C",
    unit = "g",
    type = NutrientType.BASIC,
    isPremium = false
)

val sampleCarbs = NutrientIntake(
    nutrient = carbs,
    goal = 300.0,
    intake = 215.5
)