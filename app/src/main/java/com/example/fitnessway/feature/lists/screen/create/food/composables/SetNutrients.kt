package com.example.fitnessway.feature.lists.screen.create.food.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodCreationNutrientField
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.UNutrient.Ui.NutrientLabelsFlowRow
import com.example.fitnessway.util.UNutrient.getNutrientCategoryTitle

@Composable
fun SetNutrients(
    fields: List<FoodCreationNutrientField>,
    nutrientsWithoutGoal: Pair<NutrientType, List<Nutrient>>
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        fields.forEach { field ->
            FoodCreationFormField(field)
        }

        if (nutrientsWithoutGoal.second.isNotEmpty()) {
            Box(modifier = Modifier.areaContainer(size = AreaContainerSize.SMALL)) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val moreThanOne = nutrientsWithoutGoal.second.size > 1
                    val category = getNutrientCategoryTitle(nutrientsWithoutGoal.first)
                    val nutrientsType = category.lowercase().dropLast(if (moreThanOne) 0 else 1)

                    val messageText = if (moreThanOne) {
                        "These $nutrientsType are missing goals. Setting their goal will allow them to be " +
                                "added to your food"
                    } else "This $nutrientsType is missing a goal. Setting its goal will allow it to be " +
                            "added to your food"

                    Text(
                        text = messageText,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    NutrientLabelsFlowRow(
                        nutrients = nutrientsWithoutGoal.second,
                        textStyle = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}