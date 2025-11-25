package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import com.example.fitnessway.data.model.nutrient.NutrientIntake
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Nutrient.NutrientData
import com.example.fitnessway.util.Nutrient.getNutrientColor
import kotlin.math.roundToInt

class IntakesComposables(private val intake: NutrientIntake) {
    val nutrientColor = getNutrientColor(intake.nutrient.hexColor) ?: Color(0xFFFFFFFF)

    @Composable
    fun IntakeGoal() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                Text(
                    text = if (intake.goal != null) doubleFormatter(intake.goal) else "0",
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FontFamily.Default,
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
    fun IntakeProgress(data: NutrientData, modifier: Modifier = Modifier) {
        Text(
            text = doubleFormatter(data.intake),
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Default,
            color = WhiteFont,
            modifier = modifier,
        )
    }

    @Composable
    fun IntakeName(data: NutrientData) {
        val name = if (intake.nutrient.type == NutrientType.VITAMIN
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
                    fontFamily = FontFamily.Default,
                    color = nutrientColor.copy(0.5f)
                )

                Text(
                    text = name,
                    style = MaterialTheme.typography.labelMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        )
    }
}