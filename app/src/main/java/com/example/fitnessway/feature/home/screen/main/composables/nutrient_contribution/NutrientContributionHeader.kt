package com.example.fitnessway.feature.home.screen.main.composables.nutrient_contribution

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.NutrientData
import com.example.fitnessway.data.model.m_26.NutrientDataAmount
import com.example.fitnessway.ui.shared.SuccessIcon
import com.example.fitnessway.util.extensions.getIntakeCalculation

@Composable
fun NutrientContributionHeader(
    logs: List<FoodLog>,
    nutrientData: NutrientData,
    nutrientColor: Color
) {

    val intakeCalculation = NutrientDataAmount(
        data = nutrientData,
        amount = logs
            .map {
                it.edibleInformation.nutrients
                    .toList()
                    .find { n -> n.data.base.id == nutrientData.base.id }
                    ?.amount ?: 0.0
            }
            .sumOf { it }
    ).getIntakeCalculation()

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("${nutrientData.base.name} ")
                    }
                } + amountAnnotatedString(
                    amount = nutrientData.preferences.goal ?: 0.0,
                    type = nutrientData.base.unit.toString().lowercase(),
                    color = nutrientColor
                ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = nutrientColor
            )

            if (intakeCalculation.over > 0.0) {
                SuccessIcon(
                    tint = nutrientColor,
                    size = MaterialTheme.typography.bodySmall.fontSize.value.dp
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NutrientContributionTotal(
                    logs = logs,
                    nutrientData = nutrientData,
                    textColor = nutrientColor
                )
            }

            when {
                intakeCalculation.remaining > 0.0 -> IntakeInformationWrapper(
                    information = { textStyle, fontWeight ->
                        Text(
                            text = buildAnnotatedString {
                                append("< ")
                            } + amountAnnotatedString(
                                amount = intakeCalculation.remaining,
                                type = nutrientData.base.unit.toString().lowercase()
                            ),
                            style = textStyle,
                            fontWeight = fontWeight
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                intakeCalculation.over > 0.0 -> IntakeInformationWrapper(
                    information = { textStyle, fontWeight ->
                        Text(
                            text = buildAnnotatedString {
                                append("> ")
                            } + amountAnnotatedString(
                                amount = intakeCalculation.over,
                                type = nutrientData.base.unit.toString().lowercase()
                            ),
                            style = textStyle,
                            fontWeight = fontWeight
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}