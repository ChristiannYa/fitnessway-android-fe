package com.example.fitnessway.feature.home.screen.main.composables.nutrient_contribution

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.ServingUnit
import com.example.fitnessway.ui.theme.AppModifiers
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.UNutrient
import com.example.fitnessway.util.extensions.getIntakeCalculation
import com.example.fitnessway.util.toEnum
import kotlin.time.Instant

@Composable
fun NutrientContributionOverview(
    logs: List<FoodLog>,
    nutrientData: MNutrient.Model.NutrientWithPreferences,
    nutrientColor: Color,
    formatTime: (Instant) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .areaContainer(
                    size = AppModifiers.AreaContainerSize.EXTRA_SMALL,
                    areaColor = Color.Transparent,
                    borderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.1f),
                    borderWidth = 2.dp
                )
        ) {
            logs.forEach { log ->
                log.edibleInformation.nutrients
                    .toList()
                    .find { n -> n.data.base.id == nutrientData.nutrient.id }
                    ?.getIntakeCalculation()
                    ?.let { intakeCalculation ->
                        LogData(
                            log = log,
                            intakeCalculation = intakeCalculation,
                            nutrientUnit = nutrientData.nutrient.unit.toEnum(),
                            formatTime = formatTime,
                        )
                    }
            }
        }

        NutrientContributionTotal(
            logs = logs,
            nutrientData = nutrientData,
        ) { intakeTotal, progressTotal ->
            Row {
                IntakeInformationWrapper(
                    information = { textStyle, fontWeight ->
                        Text(
                            text = amountAnnotatedString(
                                amount = intakeTotal,
                                type = nutrientData.nutrient.unit.lowercase(),
                                color = nutrientColor
                            ),
                            style = textStyle,
                            fontWeight = fontWeight,
                            color = nutrientColor
                        )
                    },
                    withContainer = false,
                    modifier = Modifier.weight(1f)
                )

                IntakeInformationWrapper(
                    information = { textStyle, fontWeight ->
                        Text(
                            text = amountAnnotatedString(
                                amount = progressTotal,
                                type = "%",
                                color = nutrientColor
                            ),
                            style = textStyle,
                            fontWeight = fontWeight,
                            color = nutrientColor
                        )
                    },
                    withContainer = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LogData(
    intakeCalculation: UNutrient.NutrientIntakeCalculation,
    log: FoodLog,
    nutrientUnit: ServingUnit,
    formatTime: (Instant) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = log.edibleInformation.base.name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            IntakeInformationWrapper(
                information = { textStyle, fontWeight ->
                    Text(
                        text = amountAnnotatedString(
                            amount = intakeCalculation.intake,
                            type = nutrientUnit.name.lowercase(),
                        ),
                        style = textStyle,
                        fontWeight = fontWeight,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.weight(1f)
            )

            IntakeInformationWrapper(
                information = { textStyle, fontWeight ->
                    Text(
                        text = amountAnnotatedString(
                            amount = intakeCalculation.progress,
                            type = "%",
                        ),
                        style = textStyle,
                        fontWeight = fontWeight,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
