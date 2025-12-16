package com.example.fitnessway.feature.profile.screen.colors.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.NutrientColorUpdateField
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge
import com.example.fitnessway.util.Nutrient.Ui.NutrientCategoryTitle
import com.example.fitnessway.util.Nutrient.getNutrientColor

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
                                                        val color = getNutrientColor(field.value) ?: MaterialTheme.colorScheme.primary

                                                        key(field.name.nutrientData.nutrient.id) {
                                                            Row(
                                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                modifier = Modifier.fillMaxWidth(),
                                                                content = {
                                                                    val colorPreviewShape = RoundedCornerShape(5.dp)

                                                                    NutrientColorUpdateFormField(
                                                                        field = field,
                                                                        modifier = Modifier
                                                                            .weight(1f)
                                                                    )

                                                                    Box(
                                                                        modifier = Modifier
                                                                            .clip(colorPreviewShape)
                                                                            .size(18.dp)
                                                                            .background(
                                                                                color = color,
                                                                                shape = colorPreviewShape
                                                                            )
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
                            )
                        }
                    }
                }
            )
        }
    )
}