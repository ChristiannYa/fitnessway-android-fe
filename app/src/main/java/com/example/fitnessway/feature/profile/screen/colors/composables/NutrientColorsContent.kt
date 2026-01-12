package com.example.fitnessway.feature.profile.screen.colors.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.NutrientColorUpdateField
import com.example.fitnessway.data.model.MNutrient.Enum.NutrientType
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.UNutrient.Ui.NutrientCategoryTitle
import com.example.fitnessway.util.UNutrient.getColor

@Composable
fun NutrientColorsContent(
    fields: Map<NutrientType, List<NutrientColorUpdateField>>,
    modifier: Modifier = Modifier
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        fields.forEach { (type, colorFields) ->
            Box(modifier = Modifier.areaContainer()) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    NutrientCategoryTitle(type)

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        colorFields.forEach { field ->
                            val color = getColor("#" + field.value)

                            val borderColor = if (color == null) {
                                MaterialTheme.colorScheme.primary
                            } else Color.Transparent

                            key(field.name.nutrientData.nutrient.id) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val colorPreviewShape = RoundedCornerShape(5.dp)

                                    NutrientColorUpdateFormField(
                                        field = field,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(colorPreviewShape)
                                            .size(21.dp)
                                            .border(
                                                width = 1.dp,
                                                color = borderColor,
                                                shape = colorPreviewShape
                                            )
                                            .background(
                                                color = color ?: Color.Transparent,
                                                shape = colorPreviewShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}