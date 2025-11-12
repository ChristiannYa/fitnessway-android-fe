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
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Nutrient.NutrientData
import kotlin.math.roundToInt

class IntakesComposables(private val intake: NutrientIntake) {
    @Composable
    fun intakeGoal() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                Text(
                    text = if (intake.goal != null) doubleFormatter(intake.goal) else "0",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = intake.nutrient.unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
                )
            }
        )
    }

    @Composable
    fun intakeProgress(data: NutrientData, modifier: Modifier = Modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier, // When using it: modifier.align(Alignment.Center),
            content = {
                Text(
                    text = doubleFormatter(data.intake),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "${data.progress.roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
                )
            }
        )
    }

    @Composable
    fun intakeName() {
        val text = if (intake.nutrient.type == NutrientType.VITAMIN
            || intake.nutrient.type == NutrientType.MINERAL
        ) {
            intake.nutrient.symbol ?: intake.nutrient.name
        } else intake.nutrient.name

        Text(
            text = text,
            color = MaterialTheme.colorScheme.inverseSurface,
            style = MaterialTheme.typography.labelMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}