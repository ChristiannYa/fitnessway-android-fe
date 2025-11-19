package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodNutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerMedium
import com.example.fitnessway.util.Formatters.doubleFormatter

private class Field(
    val key: String,
    val value: String,
    val isEditable: Boolean? = false
)

@Composable
fun EditionMode(
    food: FoodInformation
) {
    val baseFields = getBaseFields(food)

    val nutrients = listOf(
        NutrientType.BASIC to food.nutrients.basic,
        NutrientType.VITAMIN to food.nutrients.vitamin,
        NutrientType.MINERAL to food.nutrients.mineral
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        content = {
            Column(
                modifier = Modifier.areaContainerMedium(),
                content = {
                    EditableStrips(
                        title = "Food Details",
                        items = baseFields
                    )
                }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.areaContainerMedium(),
                content = {
                    nutrients.forEach { (type, ns) ->
                        if (ns.isNotEmpty()) {
                            val title = type.name.lowercase()
                                .replaceFirstChar { it.uppercase() }

                            val titleDynamic = when (type) {
                                NutrientType.BASIC -> "Nutrient"
                                NutrientType.VITAMIN -> title
                                NutrientType.MINERAL -> title
                            }

                            val fields = getNutrientFields(ns)

                            EditableStrips(
                                title = "${titleDynamic}s",
                                items = fields
                            )
                        }
                    }
                }
            )
        }
    )
}

@Composable
private fun EditableStrips(
    title: String,
    items: List<Field>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                content = {
                    val shape = 12.dp

                    items.forEach { item ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            content = {
                                Text(
                                    text = item.key,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(end = 16.dp)
                                )
                                Text(
                                    text = item.value,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Default,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(shape))
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(shape)
                                        )
                                        .padding(10.dp)
                                )
                            }
                        )
                    }
                }
            )
        }
    )
}

private fun getBaseFields(food: FoodInformation): List<Field> {
    return listOf(
        Field(
            key = "Name",
            value = food.information.name
        ),
        Field(
            key = "Brand",
            value = food.information.brand ?: "N/A"
        ),
        Field(
            key = "Amount Per Serving",
            value = doubleFormatter(food.information.amountPerServing)
        ),
        Field(
            key = "Serving Unit",
            value = food.information.servingUnit
        )
    )
}

private fun getNutrientFields(
    nutrients: List<FoodNutrientAmountData>
): List<Field> {
    return nutrients.map { (nutrient, amount) ->
        val key =
            if (nutrient.type == NutrientType.BASIC || nutrient.type == NutrientType.VITAMIN) {
                "${nutrient.name} ${nutrient.unit}"
            } else "${nutrient.name} (${nutrient.symbol}) ${nutrient.unit}"

        Field(
            key = key,
            value = doubleFormatter(amount)
        )
    }
}