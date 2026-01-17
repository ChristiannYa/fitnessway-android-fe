package com.example.fitnessway.feature.profile.screen.colors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MNutrient.Enum.NutrientType
import com.example.fitnessway.feature.profile.screen.colors.composables.NutrientColorsContent
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Loading.Area
import com.example.fitnessway.ui.shared.Messages
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Formatters.formatUiErrorMessage
import com.example.fitnessway.util.UNutrient.combine
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
        }
    ) { focusManager ->
        if (user != null) {
            when (nutrientsState) {
                is UiState.Loading -> Area("Loading nutrient colors")

                is UiState.Success -> {
                    val nutrients = nutrientsState.data

                    colorsEditionFormState?.let { formState ->
                        val fieldsProvider = NutrientColorsFieldsProvider(
                            formState = formState,
                            isFormSubmitting = nutrientColorsSetUiState is UiState.Loading,
                            focusManager = focusManager,
                            onFieldUpdate = { fieldName, value ->
                                viewModel.updateColorsEditionFormField(
                                    fieldName = fieldName,
                                    input = value
                                )
                            }
                        )

                        val fields = NutrientType.entries.associateWith { type ->
                            filterNutrientsByType(nutrientsState.data, type)
                                .map {
                                    fieldsProvider.nutrientColor(
                                        nutrientData = it,
                                        isLastField = nutrients.combine().last() == it
                                    )
                                }
                        }

                        val scrollState = rememberScrollState()

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.imePadding()
                        ) {
                            ErrorBannerAnimated(
                                isVisible = nutrientColorsUpdateErrMsg != null,
                                text = nutrientColorsUpdateErrMsg ?: ""
                            )

                            NutrientColorsContent(
                                fields = fields,
                                modifier = Modifier
                                    .verticalScroll(scrollState)
                            )
                        }
                    } ?: Area()
                }

                else -> {}
            }

            Messages.NotFoundMessageWithRetryAnimated(
                isVisible = nutrientsState is UiState.Error,
                message = formatUiErrorMessage(nutrientsState),
                onRetry = viewModel::refreshNutrients
            )

        } else NotFoundMessage("User not found")
    }
}