package com.example.fitnessway.feature.profile.screen.colors

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.profile.screen.colors.composables.NutrientColorsContent
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.ActionButton
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.TextWithLoadingIndicator
import com.example.fitnessway.util.Nutrient.filterNutrientsByType
import com.example.fitnessway.util.Nutrient.sortNutrientWithPreferencesByPremiumStatus
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.NutrientColorsFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileColorsScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val colorsEditionFormState by viewModel.colorsEditionFormState.collectAsState()
    val isColorsFormValid by viewModel.isColorsFormValid.collectAsState()

    val nutrientsState = nutrientRepoUiState.nutrientsUiState
    val user = viewModel.user

    LaunchedEffect(nutrientsState) {
        if (nutrientsState is UiState.Success) {
            viewModel.initNutrientColorsForm(
                nutrientsData = nutrientsState.data
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getNutrients()
    }

    Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                title = "Color Palette",
                extraContent = {
                    ActionButton(
                        onClick = {
                            viewModel.setColorsThatChanged()
                            viewModel.setNutrientColors()
                        },
                        text = "Update",
                        enabled = isColorsFormValid
                    )
                }
            )
        },

        content = {
            if (user != null) {
                when (nutrientsState) {
                    is UiState.Loading -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize(),
                            content = {
                                TextWithLoadingIndicator("Loading nutrients data")
                            }
                        )
                    }

                    is UiState.Success -> {
                        colorsEditionFormState?.let { formState ->
                            val fields = remember(nutrientsState.data, formState) {
                                val fieldsProvider = NutrientColorsFieldsProvider(
                                    formState = formState,
                                    onFieldUpdate = { fieldName, value ->
                                        viewModel.updateColorsEditionFormField(
                                            fieldName = fieldName,
                                            input = value
                                        )
                                    }
                                )

                                val fieldsByTypeMap = NutrientType.entries.associateWith { type ->
                                    filterNutrientsByType(nutrientsState.data, type)
                                        .sortNutrientWithPreferencesByPremiumStatus(user.isPremium)
                                        .map { fieldsProvider.nutrientColor(it) }
                                }

                                fieldsByTypeMap
                            }

                            Column(
                                content = {
                                    NutrientColorsContent(fields)
                                }
                            )
                        } ?: NotFoundText("Form data not found")
                    }

                    else -> NotFoundText(text = "Something went wrong")
                }
            } else {
                NotFoundText("User not found")
            }
        }
    )
}