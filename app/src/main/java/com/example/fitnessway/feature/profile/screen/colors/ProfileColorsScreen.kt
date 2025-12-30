package com.example.fitnessway.feature.profile.screen.colors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.profile.screen.colors.composables.NutrientColorsContent
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Loading.LoadingArea
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Formatters.formatUiErrorMessage
import com.example.fitnessway.util.UNutrient.filterNutrientsByType
import com.example.fitnessway.util.Ui.handleTempApiErrorMessage
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.NutrientColorsFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileColorsScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val colorsEditionFormState by viewModel.colorsEditionFormState.collectAsState()
    val isColorsFormValid by viewModel.isColorsFormValid.collectAsState()

    val user = viewModel.user
    val nutrientsState = nutrientRepoUiState.nutrientsUiState
    val nutrientColorsSetUiState = uiState.nutrientColorsSetUiState

    val nutrientColorsUpdateErrMsg = handleTempApiErrorMessage(
        uiState = nutrientColorsSetUiState,
        onTimeOut = viewModel::resetNutrientColorsUpdateState
    )

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
                onBackClick = {
                    if (nutrientColorsSetUiState is UiState.Error) {
                        viewModel.resetNutrientColorsUpdateState()
                    }

                    onBackClick()
                },
                isOnBackEnabled = nutrientColorsSetUiState !is UiState.Loading,
                title = "Color Palette",
                extraContent = {
                    if (nutrientsState is UiState.Success) {
                        Clickables.HeaderDoneButton(
                            onClick = {
                                viewModel.setColorsThatChanged()
                                viewModel.setNutrientColors()
                            },
                            enabled = isColorsFormValid,
                            isLoading = nutrientColorsSetUiState is UiState.Loading
                        )
                    }
                }
            )
        },

        content = {
            if (user != null) {
                when (nutrientsState) {
                    is UiState.Loading -> LoadingArea("Loading nutrient colors")

                    is UiState.Success -> {
                        colorsEditionFormState?.let { formState ->
                            val fields = remember(
                                nutrientsState.data,
                                formState,
                                nutrientColorsSetUiState
                            ) {
                                val fieldsProvider = NutrientColorsFieldsProvider(
                                    formState = formState,
                                    isFormSubmitting = nutrientColorsSetUiState is UiState.Loading,
                                    onFieldUpdate = { fieldName, value ->
                                        viewModel.updateColorsEditionFormField(
                                            fieldName = fieldName,
                                            input = value
                                        )
                                    }
                                )

                                val fieldsByTypeMap = NutrientType.entries.associateWith { type ->
                                    filterNutrientsByType(nutrientsState.data, type)
                                        .map { fieldsProvider.nutrientColor(it) }
                                }

                                fieldsByTypeMap
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                ErrorBannerAnimated(
                                    isVisible = nutrientColorsUpdateErrMsg != null,
                                    text = nutrientColorsUpdateErrMsg ?: ""
                                )

                                NutrientColorsContent(fields)
                            }
                        } ?: LoadingArea()
                    }

                    else -> {}
                }

                NotFoundMessageAnimated(
                    isVisible = nutrientsState is UiState.Error,
                    message = formatUiErrorMessage(nutrientsState)
                )

            } else NotFoundMessage("User not found")
        }
    )
}