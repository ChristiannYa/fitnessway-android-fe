package com.example.fitnessway.feature.lists.screen.details.edition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MNutrient.Enum.NutrientType
import com.example.fitnessway.feature.lists.screen.details.edition.composables.FoodDetailsField
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.util.UNutrient.combine
import com.example.fitnessway.util.Ui.handleTempApiErrorMessage
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodEditionFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodEditionScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val foodEditionFormState by viewModel.foodEditionFormState.collectAsState()
    val selectedFood by viewModel.selectedFood.collectAsState()
    val deletedNutrients by viewModel.deletedNutrients.collectAsState()

    val user = viewModel.user
    val foodUpdateState = uiState.foodUpdateState

    val foodUpdateErrorMessage = handleTempApiErrorMessage(
        uiState = foodUpdateState,
        onTimeOut = viewModel::resetFoodUpdateState
    )

    val selectedFoodCopy = selectedFood
    val foodEditionFormStateCopy = foodEditionFormState
    val areDelegatesPresent = selectedFoodCopy != null
            && foodEditionFormStateCopy != null

    val title = "Food Edition"
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    if (user != null) {
        if (areDelegatesPresent) {
            val nutrients = listOf(
                Triple(
                    NutrientType.BASIC,
                    selectedFoodCopy.nutrients.basic,
                    "Summary"
                ),
                Triple(
                    NutrientType.VITAMIN,
                    selectedFoodCopy.nutrients.vitamin,
                    "Vitamins"
                ),
                Triple(
                    NutrientType.MINERAL,
                    selectedFoodCopy.nutrients.mineral,
                    "Minerals"
                )
            )

            val fieldsProvider = FoodEditionFieldsProvider(
                formState = foodEditionFormStateCopy,
                focusManager = focusManager,
                isFormSubmitting = foodUpdateState is UiState.Loading,
                onFieldUpdate = { fieldName, value ->
                    viewModel.updateFoodEditionFormField(
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

            val allNutrientIds = selectedFoodCopy.nutrients.combine().map {
                it.nutrientWithPreferences.nutrient.id
            }.filter { it !in deletedNutrients }

            val nutrientFields = nutrients.map { (type, ns, title) ->
                val fields = ns
                    .filter { it.nutrientWithPreferences.nutrient.id !in deletedNutrients }
                    .map { nutrientData ->
                        val nutrient = nutrientData.nutrientWithPreferences.nutrient

                        fieldsProvider.nutrient(
                            nutrient = nutrientData.nutrientWithPreferences.nutrient,
                            isLastField = nutrient.id == allNutrientIds.last()
                        )
                    }

                Triple(type, fields, title)
            }

            Screen(
                header = {
                    Header(
                        onBackClick = {
                            if (foodUpdateState is UiState.Error) {
                                viewModel.resetFoodUpdateState()
                            }

                            viewModel.resetDeletedNutrients()
                            onBackClick()
                        },
                        title = title
                    ) {
                        Clickables.HeaderDoneButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.updateFood()
                                viewModel.resetDeletedNutrients()
                            },
                            enabled = viewModel.isFoodEditionFormValid
                        )
                    }
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.imePadding()
                ) {
                    ErrorBannerAnimated(
                        isVisible = foodUpdateErrorMessage != null,
                        text = foodUpdateErrorMessage ?: ""
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.verticalScroll(scrollState)
                    ) {
                        FieldSection(
                            title = "Details",
                            fields = detailFields,
                            content = { FoodDetailsField(it) }
                        )

                        nutrientFields.forEach { (_, fields, title) ->
                            FieldSection(
                                title = title,
                                fields = fields,
                                content = {
                                    FoodDetailsField(
                                        field = it,
                                        onRemoveNutrient = viewModel::filterNutrientFromForm
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

    } else NotFoundScreen(
        onBackClick = onBackClick,
        title = title,
        message = "User not found"
    )
}

@Composable
private fun<T> FieldSection(
    title: String,
    fields: List<T>,
    content: @Composable (T) -> Unit
) {
    if (fields.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                fields.forEach { content(it) }
            }
        }
    }
}
