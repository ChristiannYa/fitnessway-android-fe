package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.example.fitnessway.data.model.nutrient.NutrientIntake
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Nutrient.NutrientData
import com.example.fitnessway.util.Nutrient.getNutrientColor
import kotlin.math.roundToInt

class IntakesComposables(private val intake: NutrientIntake) {
    val nutrientColor = getNutrientColor(intake.nutrient.hexColor)

    @Composable
    fun intakeGoal() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                Text(
                    text = if (intake.goal != null) doubleFormatter(intake.goal) else "0",
                    style = MaterialTheme.typography.labelLarge,
                    color = nutrientColor
                )
                Text(
                    text = intake.nutrient.unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = nutrientColor.copy(0.5f)
                )
            }
        )
    }

    @Composable
    fun intakeProgress(data: NutrientData, modifier: Modifier = Modifier) {
        Text(
            text = doubleFormatter(data.intake),
            style = MaterialTheme.typography.labelLarge,
            modifier = modifier,
            color = WhiteFont
        )
    }

    @Composable
    fun intakeName(data: NutrientData) {
        val text = if (intake.nutrient.type == NutrientType.VITAMIN
            || intake.nutrient.type == NutrientType.MINERAL
        ) {
            intake.nutrient.symbol ?: intake.nutrient.name
        } else intake.nutrient.name

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = {
                Text(
                    text = "${data.progress.roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = nutrientColor.copy(0.5f)
                )

                Text(
                    text = text,
                    color = nutrientColor,
                    style = MaterialTheme.typography.labelMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        )
    }
}