package com.example.fitnessway.feature.profile.screen.goals.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.NutrientGoalEditionField
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.AppModifiers.blurPremiumItem
import com.example.fitnessway.util.UNutrient.Ui.NutrientCategoryTitle

@Composable
fun NutrientGoalsContent(
    nutrientFields: Map<NutrientType, List<NutrientGoalEditionField>>,
    premiumNutrientsMap: Map<NutrientType, List<Nutrient>>,
    isUserPremium: Boolean
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            nutrientFields.forEach { (type, goalFields) ->
                item(key = type) {
                    Box(
                        modifier = Modifier.areaContainer(),
                        content = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                content = {
                                    NutrientCategoryTitle(type)

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        content = {
                                            goalFields.forEach { field ->
                                                val enabled =
                                                    (!field.name.nutrientData.nutrient.isPremium || isUserPremium)

                                                key(field.name.nutrientData.nutrient.id) {
                                                    GoalsEditionFormField(
                                                        field = field,
                                                        enabled = enabled,
                                                        isUserPremium = isUserPremium,
                                                        modifier = Modifier.blurPremiumItem(!enabled)
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            }

            if (!isUserPremium) {
                item {
                    UpgradePromptSection(
                        premiumNutrientsMap = premiumNutrientsMap
                    )
                }
            }
        }
    )
}