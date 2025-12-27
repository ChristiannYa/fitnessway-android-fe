package com.example.fitnessway.feature.profile.screen.goals.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.ui.shared.PremiumIcon
import com.example.fitnessway.ui.theme.AppModifiers
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.UNutrient.Ui.NutrientLabelsFlowRow
import com.example.fitnessway.util.UNutrient.getNutrientCategoryTitle
import com.example.fitnessway.util.Ui.Measurements.TEXT_ICON_HORIZONTAL_SPACE

@Composable
fun UpgradePromptSection(
    premiumNutrientsMap: Map<NutrientType, List<Nutrient>>,
    modifier: Modifier = Modifier
) {
    val sectionAccent = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = modifier
            .areaContainer(
                size = AppModifiers.AreaContainerSize.LARGE,
                borderWidth = 2.dp,
                borderColor = sectionAccent,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(TEXT_ICON_HORIZONTAL_SPACE),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PremiumIcon()

            Text(
                text = "Available with Premium",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Text(
            text = "Upgrade to set goals for additional nutrients",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
        )

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            premiumNutrientsMap.forEach { (type, nutrients) ->
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = getNutrientCategoryTitle(type),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    NutrientLabelsFlowRow(nutrients, color = sectionAccent)
                }
            }
        }
    }
}