package com.example.fitnessway.util.food.creation

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
import com.example.fitnessway.data.model.MNutrient.Enum.NutrientType
import com.example.fitnessway.data.model.MNutrient.Model.Nutrient
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.INutrientDvControls
import com.example.fitnessway.util.UNutrient.Ui.NutrientLabelsFlowRow
import com.example.fitnessway.util.UNutrient.toReadable
import com.example.fitnessway.util.form.field.FoodCreationNutrientField

@Composable
fun SetNutrients(
    fields: List<FoodCreationNutrientField>,
    nutrientsWithoutGoal: Pair<NutrientType, List<Nutrient>>,
    nutrientDvControls: INutrientDvControls.NutrientDvControls? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        fields.forEach { FoodCreationFormField(it, nutrientDvControls) }

        if (nutrientsWithoutGoal.second.isNotEmpty()) {
            Box(modifier = Modifier.areaContainer(size = AreaContainerSize.SMALL)) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val moreThanOne = nutrientsWithoutGoal.second.size > 1
                    val nutrientsType = nutrientsWithoutGoal.first.toReadable(moreThanOne, true)

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