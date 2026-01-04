package com.example.fitnessway.feature.profile.screen.goals.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.NutrientGoalEditionField
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.UNutrient.Ui.NutrientCategoryTitle

@Composable
fun NutrientFields(
    nutrientFields: Map<NutrientType, List<NutrientGoalEditionField>>,
    isUserPremium: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        nutrientFields.forEach { (type, goalFields) ->
            Box(modifier = Modifier.areaContainer()) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    NutrientCategoryTitle(type)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        goalFields.forEach { field ->
                            key(field.name.nutrientData.nutrient.id) {
                                GoalsEditionFormField(
                                    field = field,
                                    isUserPremium = isUserPremium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}