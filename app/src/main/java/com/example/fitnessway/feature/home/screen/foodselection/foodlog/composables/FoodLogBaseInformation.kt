package com.example.fitnessway.feature.home.screen.foodselection.foodlog.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.form.FoodLogField
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Food
import com.example.fitnessway.util.form.field.provider.FoodLogFieldsProvider

@Composable
fun FoodLogInformationList(
    food: FoodInformation,
    category: String,
    fieldsProvider: FoodLogFieldsProvider,
    isEditing: Boolean
) {
    val spacing = 8.dp

    val fields = getFoodLogFormFields(
        fieldsProvider = fieldsProvider,
        servingUnit = food.information.servingUnit
    )

    Column(
        modifier = Modifier.areaContainer(AreaContainerSize.LARGE),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val brand = Food.Ui.getFoodBrandText(food.information.brand)
                val brandColor = Food.Ui.getFoodBrandColor()

                Text(
                    text = brand,
                    style = MaterialTheme.typography.labelMedium,
                    color = brandColor,
                )

                Text(
                    text = food.information.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    fields.forEachIndexed { index, field ->
                        FoodLogInformation(field, isEditing)

                        if (index < fields.lastIndex && !isEditing) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = spacing),
                                color = MaterialTheme.colorScheme.onBackground.copy(0.1f)
                            )
                        }
                    }
                }
            )
        }
    )
}


@Composable
fun FoodLogInformation(
    field: FoodLogField,
    isEditing: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Text(
                text = field.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(end = 16.dp)
            )

            FoodLogBaseInformationField(field, isEditing)
        }
    )
}

@Composable
private fun getFoodLogFormFields(
    fieldsProvider: FoodLogFieldsProvider,
    servingUnit: String,
): List<FoodLogField> {
    return listOf(
        fieldsProvider.servings(),
        fieldsProvider.amountPerServing(servingUnit),
        fieldsProvider.time()
    )
}