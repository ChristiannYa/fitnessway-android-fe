package com.example.fitnessway.feature.lists.screen.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.lists.screen.details.composables.EditionMode
import com.example.fitnessway.feature.lists.screen.details.composables.FoodInformation
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Nutrient.filterNutrientsByType
import com.example.fitnessway.util.form.field.provider.FoodEditionFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val selectedFood by viewModel.selectedFood.collectAsState()
    val foodEditionFormState by viewModel.foodEditionFormState.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    LaunchedEffect(selectedFood) {
        selectedFood?.let { food ->
            viewModel.initializeFoodForm(food)
        }
    }

    Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                isOnBackEnabled = !isEditing
            )
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

                    val detailFields = listOf(
                        fieldsProvider.name(),
                        fieldsProvider.brand(),
                        fieldsProvider.amountPerServing(),
                        fieldsProvider.servingUnit()
                    )

                    val summaryFields = filterNutrientsByType(
                        nutrients = food.nutrients,
                        type = NutrientType.BASIC
                    ).map { fieldsProvider.nutrient(it.nutrient) }

                    val vitaminFields = filterNutrientsByType(
                        nutrients = food.nutrients,
                        type = NutrientType.VITAMIN
                    ).map { fieldsProvider.nutrient(it.nutrient) }

                    val mineralFields = filterNutrientsByType(
                        nutrients = food.nutrients,
                        type = NutrientType.MINERAL
                    ).map { fieldsProvider.nutrient(it.nutrient) }

                    Box(modifier = Modifier.fillMaxSize()) {
                        FoodInformation(
                            food = food,
                            onEdit = { viewModel.toggleEditionMode() },
                            isEditing = isEditing
                        )

                        AnimatedVisibility (
                            visible = isEditing,
                            enter = slideInVertically(
                                initialOffsetY = { fullHeight -> fullHeight },
                                animationSpec = tween(durationMillis = 300)
                            ),
                            exit = slideOutVertically(
                                targetOffsetY = { fullHeight -> fullHeight },
                                animationSpec = tween(durationMillis = 300)
                            ),
                            content = {
                                EditionMode(
                                    foodDetailFields = detailFields,
                                    foodSummaryFields = summaryFields,
                                    foodVitaminFields = vitaminFields,
                                    foodMineralFields = mineralFields,
                                    onDone = { viewModel.toggleEditionMode() }
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}
