package com.example.fitnessway.feature.lists.screen.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.lists.screen.details.composables.EditionMode
import com.example.fitnessway.feature.lists.screen.details.composables.FoodInformation
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.form.field.provider.FoodEditionFieldsProvider
import org.koin.androidx.compose.koinViewModel
import com.example.fitnessway.util.Nutrient.filterNutrientsByType

@Composable
fun DetailsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val selectedFood by viewModel.selectedFood.collectAsState()
    val foodEditionFormState by viewModel.foodEditionFormState.collectAsState()

    LaunchedEffect(selectedFood) {
        selectedFood?.let { food ->
            viewModel.initializeFoodForm(food)
        }
    }

    Screen(
        header = {
            Header(onBackClick)
        },
        content = {
            val food = selectedFood

            if (food == null) {
                Text("No food selected")
            } else {
                foodEditionFormState?.let { formState ->
                    val fieldsProvider = FoodEditionFieldsProvider(
                        formState = formState,
                        onFieldUpdate = { fieldName, value ->
                            viewModel.updateFoodCreationFormField(
                                fieldName = fieldName,
                                input = value
                            )
                        }
                    )

                    val summary = filterNutrientsByType(
                            nutrients = food.nutrients,
                    type = NutrientType.BASIC
                    )

                    val vitamins = filterNutrientsByType(
                        nutrients = food.nutrients,
                        type = NutrientType.VITAMIN
                    )

                    val minerals = filterNutrientsByType(
                        nutrients = food.nutrients,
                        type = NutrientType.MINERAL
                    )

                    val foodDetailFields = listOf(
                        fieldsProvider.name(),
                        fieldsProvider.brand(),
                        fieldsProvider.amountPerServing(),
                        fieldsProvider.servingUnit()
                    )

                    val foodSummaryFields = summary.map {
                        fieldsProvider.nutrient(it.nutrient)
                    }

                    val foodVitaminsFields = vitamins.map {
                        fieldsProvider.nutrient(it.nutrient)
                    }

                    val foodMineralsFields = minerals.map {
                        fieldsProvider.nutrient(it.nutrient)
                    }

                    // FoodInformation(food)
                    EditionMode(
                        foodDetailFields = foodDetailFields,
                        foodSummaryFields = foodSummaryFields,
                        foodVitaminFields = foodVitaminsFields,
                        foodMineralFields = foodMineralsFields
                    )
                }
            }
        }
    )
}
