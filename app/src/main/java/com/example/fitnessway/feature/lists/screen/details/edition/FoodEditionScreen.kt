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
import com.example.fitnessway.feature.lists.screen.details.edition.composables.FoodEditionFormField
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.util.UNutrient.combine
import com.example.fitnessway.util.UNutrient.getIds
import com.example.fitnessway.util.Ui.handleTempApiErrorMessage
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodEditionFieldsProvider
import com.example.fitnessway.util.isIdle
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodEditionScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val foodEditionFormState by viewModel.foodEditionFormState.collectAsState()
    val selectedFood by viewModel.selectedFood.collectAsState()
    val deletedNutrients by viewModel.deletedNutrients.collectAsState()
    val isFoodEditionFormValid by viewModel.isFoodEditionFormValid.collectAsState()

    val user = userFlow
    val foodUpdateState = uiState.foodUpdateState
    val nutrientDvControls = viewModel.nutrientDvControls

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

            val allNutrientIds = selectedFoodCopy.nutrients
                .combine()
                .map { it.nutrientWithPreferences.nutrient }
                .getIds()
                .filter { it !in deletedNutrients }

            val nutrientFields = nutrients.map { (type, innerNutrients, title) ->
                val fields = innerNutrients
                    // Iterate over `innerNutrients` with `mapNotNull` so that null values are omitted.
                    // In order to obtain these values we use `takeIf` (which can return null values) to select
                    // items that meet its conditional, where in this case are the nutrients that are not in the
                    // `deletedNutrients` list
                    .mapNotNull { it.nutrientWithPreferences.nutrient.takeIf { n -> n.id !in deletedNutrients } }
                    // Finally, just create `fieldsProvider.nutrient` with the leftover data
                    .map { fieldsProvider.nutrient(it, it.id == allNutrientIds.last()) }

                Triple(type, fields, title)
            }

            Screen(
                header = {
                    Header(
                        onBackClick = {
                            viewModel.resetFoodEditionStates()
                            onBackClick()
                        },
                        title = title
                    ) {
                        Clickables.HeaderDoneButton(
                            enabled = isFoodEditionFormValid
                        ) {
                            focusManager.clearFocus()
                            if (!foodUpdateState.isIdle) viewModel.resetFoodUpdateState()
                            viewModel.resetDeletedNutrients()
                            viewModel.updateFood()
                        }
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
                            fields = detailFields
                        ) { FoodEditionFormField(it) }

                        nutrientFields.forEach { (_, fields, title) ->
                            FieldSection(
                                title = title,
                                fields = fields
                            ) {
                                FoodEditionFormField(
                                    field = it,
                                    onRemoveNutrient = viewModel::filterNutrientFromForm,
                                    nutrientDvControls = nutrientDvControls
                                )
                            }
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
private fun <T> FieldSection(
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
