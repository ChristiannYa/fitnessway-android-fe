package com.example.fitnessway.feature.profile.screen.goals.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.ui.shared.PremiumIcon
import com.example.fitnessway.ui.theme.AppModifiers
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Nutrient.getNutrientCategoryTitle
import com.example.fitnessway.util.Ui.Measurements.TEXT_ICON_HORIZONTAL_SPACE

@Composable
fun UpgradePromptSection(
    premiumNutrientsMap: Map<NutrientType, List<NutrientWithPreferences>>,
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
                PremiumNutrientsGroup(type, nutrients, sectionAccent)
            }
        }
    }
}

@Composable
private fun PremiumNutrientsGroup(
    type: NutrientType,
    nutrients: List<NutrientWithPreferences>,
    sectionAccent: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = getNutrientCategoryTitle(type),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            nutrients.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(50),
                    color = sectionAccent
                ) {
                    Text(
                        text = item.nutrient.name,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                    )
                }
            }
        }
    }
}