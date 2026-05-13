package com.example.fitnessway.feature.profile.screen.colors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.feature.profile.screen.colors.composables.NutrientColorFields
import com.example.fitnessway.feature.profile.screen.colors.composables.color_picker.ColorPickerPopup
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.DarkOverlay
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.consumeTap
import com.example.fitnessway.util.UNutrient.combine
import com.example.fitnessway.util.UNutrient.filterNutrientsByType
import com.example.fitnessway.util.Ui.handleTempApiErrMsg
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.util.form.field.provider.NutrientColorsFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileColorsScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val isColorsFormValid by viewModel.isColorsFormValid.collectAsState()
    val colorsEditionFormState = viewModel.colorsEditionFormState.collectAsState().value

    val nutrientsUiState = nutrientRepoUiState.nutrients
    val nutrientColorsSetUiState = uiState.nutrientColorsSetUiState

    val nutrientColorsUpdateErrMsg = handleTempApiErrMsg(
        uiState = nutrientColorsSetUiState,
        onTimeOut = viewModel::resetNutrientColorsUpdateState
    )

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
                    if (nutrientsUiState is UiState.Success) {
                        Clickables.DoneButton(
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
        Box {
            if (nutrientsUiState !is UiState.Success || colorsEditionFormState == null) return@Screen

            val nutrients = nutrientsUiState.data

            val fieldsProvider = NutrientColorsFieldsProvider(
                formState = colorsEditionFormState,
                isFormSubmitting = nutrientColorsSetUiState is UiState.Loading,
                focusManager = focusManager,
                onFieldUpdate = viewModel::updateColorsEditionFormField
            )

            val fields = NutrientType.entries.associateWith { type ->
                nutrientsUiState.data
                    .filterNutrientsByType(type)
                    .map { fieldsProvider.nutrientColor(it, nutrients.combine().last() == it) }
            }

            var fieldSelected by remember { mutableStateOf<FormFieldName.NutrientColorUpdate?>(null) }
            var hex by remember { mutableStateOf("FFFFFF") }
            var isColorPickerVisible by remember { mutableStateOf(false) }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.imePadding()
            ) {
                ErrorBannerAnimated(
                    isVisible = nutrientColorsUpdateErrMsg != null,
                    text = nutrientColorsUpdateErrMsg ?: ""
                )

                NutrientColorFields(
                    fields = fields,
                    onColorBoxTap = { fieldName, hexInput ->
                        focusManager.clearFocus()
                        hex = hexInput;
                        isColorPickerVisible = true
                        fieldSelected = fieldName
                    }
                )
            }

            DarkOverlay(
                isVisible = isColorPickerVisible,
                onClick = { isColorPickerVisible = false },
            )

            ColorPickerPopup(
                isVisible = isColorPickerVisible,
                initialHex = hex,
                onSetHex = { hexInput ->
                    fieldSelected?.let { f ->
                        hex = hexInput
                        isColorPickerVisible = false
                        viewModel.updateColorsEditionFormField(f, hexInput)
                    }
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .consumeTap(onTap = { focusManager.clearFocus() })
            )
        }
    }
}