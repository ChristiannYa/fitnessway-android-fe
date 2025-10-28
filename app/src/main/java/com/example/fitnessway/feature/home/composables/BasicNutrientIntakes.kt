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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.data.model.nutrient.NutrientIntake
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.calcNutrientData
import com.example.fitnessway.util.intakeNumStyle
import com.example.fitnessway.util.intakeTextStyle

@Composable
fun BasicNutrientIntakes(state: UiState<NutrientsByType>) {
    when (state) {
        is UiState.Loading -> Text("Loading basic nutrient intakes")
        is UiState.Success -> Nutrients(state.data)
        is UiState.Error -> ApiErrorMessage(state.message)
        UiState.Idle -> {}
    }
}

@Composable
fun Nutrients(nutrients: NutrientsByType) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.inverseSurface.copy(
                    0.01f
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            nutrients.basic.forEach { nutrient ->
                Nutrient(nutrient)
            }
        }
    )
}

@Composable
fun Nutrient(intake: NutrientIntake) {
    val nutrientData = remember(intake) {
        calcNutrientData(intake)
    }
    val barRadius: Dp = 16.dp

    Column(
        modifier = Modifier
            .width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = {
            Text(
                text = "${intake.goal}",
                style = intakeNumStyle
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(180.dp)
                    .background(
                        color = MaterialTheme.colorScheme.inverseSurface.copy(
                            0.05f
                        ),
                        shape = RoundedCornerShape(barRadius),
                    )
                    .clip(RoundedCornerShape(barRadius)),
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(nutrientData.progress / 100f)
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
                        text = "${intake.intake}",
                        style = intakeNumStyle,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-12).dp)
                    )
                }
            )

            Text(
                text = intake.nutrient.name,
                color = MaterialTheme.colorScheme.inverseSurface,
                style = intakeTextStyle,
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
        Nutrients(sampleNutrientsByType)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NutrientPreview() {
    FitnesswayTheme {
        Nutrient(sampleProtein)
    }
}

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
    goal = 2890f,
    intake = 1854f
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
    goal = 90f,
    intake = 42.6f,
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
    goal = 300f,
    intake = 215f
)