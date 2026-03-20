package com.example.fitnessway.feature.profile.screen.colors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.feature.profile.screen.colors.composables.NutrientColorsContent
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Loading.Area
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.util.UNutrient.combine
import com.example.fitnessway.util.UNutrient.filterNutrientsByType
import com.example.fitnessway.util.Ui.handleTempApiErrMsg
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.NutrientColorsFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileColorsScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val colorsEditionFormState by viewModel.colorsEditionFormState.collectAsState()
    val isColorsFormValid by viewModel.isColorsFormValid.collectAsState()

    val user = userFlow
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState
    val nutrientColorsSetUiState = uiState.nutrientColorsSetUiState

    val nutrientColorsUpdateErrMsg = handleTempApiErrMsg(
        uiState = nutrientColorsSetUiState,
        onTimeOut = viewModel::resetNutrientColorsUpdateState
    )

    val title = "Color Palette"

    if (user != null) {
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
                    title = title,
                    extraContent = {
                        if (nutrientsUiState is UiState.Success) {
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
            if (nutrientsUiState is UiState.Success) {
                val nutrients = nutrientsUiState.data

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
                        nutrientsUiState.data
                            .filterNutrientsByType(type)
                            .map { fieldsProvider.nutrientColor(it, nutrients.combine().last() == it) }
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
                            modifier = Modifier.verticalScroll(scrollState)
                        )
                    }
                } ?: Area()
            }
        }
    } else NotFoundScreen(
        onBackClick = onBackClick,
        message = "User not found",
        title = title
    )
}