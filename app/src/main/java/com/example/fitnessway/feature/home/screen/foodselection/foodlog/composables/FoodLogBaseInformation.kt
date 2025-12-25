package com.example.fitnessway.feature.home.screen.foodselection.foodlog.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
    servingsField: FoodLogField,
    amountPerServingsField: FoodLogField,
    timeField: FoodLogField
) {
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
                    FoodLogBaseInformationField(servingsField)
                    FoodLogBaseInformationField(amountPerServingsField)
                    FoodLogBaseInformationField(timeField)
                }
            )
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