package com.example.fitnessway.feature.profile.screen.colors.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.NutrientColorUpdateField
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge
import com.example.fitnessway.util.Nutrient.Ui.NutrientCategoryTitle

@Composable
fun NutrientColorsContent(
    fields: Map<NutrientType, List<NutrientColorUpdateField>>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            Text(
                text = "e.g. #87CEEB",
                style = MaterialTheme.typography.titleSmall
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    fields.forEach { (type, colorFields) ->
                        item(key = type) {
                            Box(
                                modifier = Modifier.areaContainerLarge(),
                                content = {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(20.dp),
                                        content = {
                                            NutrientCategoryTitle(type)

                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                                content = {
                                                    colorFields.forEach { field ->

                                                        key(field.name.nutrientData.nutrient.id) {
                                                            NutrientColorUpdateFormField(field)
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
                }
            )
        }
    )
}