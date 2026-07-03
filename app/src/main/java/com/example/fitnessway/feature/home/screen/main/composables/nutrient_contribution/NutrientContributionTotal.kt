package com.example.fitnessway.feature.home.screen.main.composables.nutrient_contribution

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.NutrientData
import com.example.fitnessway.util.extensions.getIntakeCalculation
import com.example.fitnessway.util.extensions.toTruncatedDecimalString

@Composable
fun NutrientContributionTotal(
    logs: List<FoodLog>,
    nutrientData: NutrientData,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,

    /**
     * First param: total intake
     * Second param: total progress
     */
    override: (@Composable (Double, Double) -> Unit)? = null
) {
    val intakeCalculations = logs.map {
        it.edibleInformation.nutrients
            .toList()
            .find { n -> n.data.base.id == nutrientData.base.id }
            ?.getIntakeCalculation()
    }

    val intakeTotal = intakeCalculations.sumOf { it?.intake ?: 0.0 }
    val progressTotal = intakeCalculations.sumOf {
        (it?.progress ?: 0.0)
            .toTruncatedDecimalString(4)
            .toDouble()
    }

    override
        ?.let { it(intakeTotal, progressTotal) }
        ?: Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IntakeInformationWrapper(
                information = { textStyle, fontWeight ->
                    Text(
                        text = amountAnnotatedString(
                            amount = intakeTotal,
                            type = nutrientData.base.unit.toString().lowercase(),
                            color = textColor
                        ),
                        style = textStyle,
                        color = textColor,
                        fontWeight = fontWeight
                    )
                },
                modifier = Modifier.weight(1f)
            )

            IntakeInformationWrapper(
                information = { textStyle, fontWeight ->
                    Text(
                        text = amountAnnotatedString(
                            amount = progressTotal,
                            type = "%",
                            color = textColor
                        ),
                        style = textStyle,
                        fontWeight = fontWeight,
                        color = textColor
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
}