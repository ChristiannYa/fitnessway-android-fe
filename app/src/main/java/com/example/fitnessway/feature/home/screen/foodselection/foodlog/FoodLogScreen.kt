package com.example.fitnessway.feature.home.screen.foodselection.foodlog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MNutrient.Model.NutrientDataWithAmount
import com.example.fitnessway.data.model.MNutrient.Model.NutrientsByType
import com.example.fitnessway.feature.home.screen.foodselection.foodlog.composables.FoodLogInformation
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Banners.SuccessBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.UFood.Ui.getFoodLogCategory
import com.example.fitnessway.util.UFood.calcNutrientIntakesFromFoodLogServings
import com.example.fitnessway.util.UNutrient
import com.example.fitnessway.util.UNutrient.Ui.PagedNutrients
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.Ui.AppLabel
import com.example.fitnessway.util.Ui.handleApiSuccessTempState
import com.example.fitnessway.util.Ui.handleTempApiErrorMessage
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodLogFieldsProvider
import com.example.fitnessway.util.isLoading
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodLogScreen(
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val foodLogFormState by viewModel.foodLogFormState.collectAsState()
    val foodLogCategory by viewModel.foodLogCategory.collectAsState()
    val selectedFoodToLog by viewModel.selectedFoodToLog.collectAsState()

    val user = userFlow
    val foodLogAddState = uiState.foodLogAddState
    val time = viewModel.getCurrentTime()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetFoodLogAddState()
        }
    }

    LaunchedEffect(selectedFoodToLog?.information?.id) {
        selectedFoodToLog?.let { food ->
            viewModel.initializeFoodLogForm(food, time)
        }
    }

    val foodLogCategoryCopy = foodLogCategory
    val foodLogFormStateCopy = foodLogFormState
    val selectedFoodToLogCopy = selectedFoodToLog

    val focusManager = LocalFocusManager.current

    val isFoodLogSuccess = handleApiSuccessTempState(
        uiState = foodLogAddState,
        onTimeout = viewModel::resetFoodLogAddState
    )

    val foodLogErrorMessage = handleTempApiErrorMessage(
        uiState = foodLogAddState,
        onTimeOut = viewModel::resetFoodLogAddState
    )

    if (user != null) {
        if (foodLogCategoryCopy != null &&
            foodLogFormStateCopy != null &&
            selectedFoodToLogCopy != null
        ) {
            val foodLogCategoryString = getFoodLogCategory(foodLogCategoryCopy)

            Screen(
                header = {
                    Header(
                        onBackClick = {
                            if (foodLogAddState is UiState.Error) viewModel.resetFoodLogAddState()
                            onBackClick()
                        },
                        isOnBackEnabled = !foodLogAddState.isLoading,
                        title = "Log Submission"
                    ) {
                        if (selectedFoodToLogCopy.metadata.isFavorite) {
                            Structure.AppIconDynamic(
                                source = Structure.AppIconButtonSource.Vector(
                                    imageVector = Icons.Default.Star
                                ),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AppLabel<Unit>(
                            text = foodLogCategoryString,
                            size = Ui.LabelSize.MEDIUM,
                        )

                        Clickables.HeaderDoneButton(
                            onClick = viewModel::addFoodLog,
                            enabled = viewModel.isFoodLogFormValid,
                            isLoading = foodLogAddState is UiState.Loading
                        )
                    }
                }
            ) {
                val scrollState = rememberScrollState()

                val fieldsProvider = FoodLogFieldsProvider(
                    formState = foodLogFormStateCopy,
                    focusManager = focusManager,
                    isFormSubmitting = foodLogAddState is UiState.Loading,
                    onFieldUpdate = { fieldName, value ->
                        viewModel.updateFoodLogFormField(fieldName, value)
                    }
                )

                val foodNutrients = remember(
                    selectedFoodToLogCopy.nutrients,
                    foodLogFormStateCopy.data.servings
                ) {
                    val servings = foodLogFormStateCopy.data.servings.toDoubleOrNull() ?: 0.0

                    calcNutrientIntakesFromFoodLogServings(
                        nutrients = selectedFoodToLogCopy.nutrients,
                        currentServings = 1.0,
                        newServings = servings
                    )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.verticalScroll(scrollState)
                    ) {
                        ErrorBannerAnimated(
                            isVisible = foodLogErrorMessage != null,
                            text = foodLogErrorMessage ?: ""
                        )

                        FoodLogInformation(
                            food = selectedFoodToLogCopy,
                            servingsField = fieldsProvider.servings(),
                            amountPerServingsField = fieldsProvider.amountPerServing(
                                servingUnit = selectedFoodToLogCopy.information.servingUnit
                            ),
                            timeField = fieldsProvider.time()
                        )

                        val sections = getNutrientSectionsConfig(foodNutrients)

                        sections.forEach { section ->
                            if (section.shouldShow) {
                                NutrientSection(
                                    title = section.title,
                                    nutrients = section.nutrients,
                                    isUserPremium = user.isPremium,
                                    isBasicNutrient = section.isBasicNutrient
                                )
                            }
                        }
                    }

                    SuccessBannerAnimated(
                        text = "Food logged successfully",
                        isVisible = isFoodLogSuccess,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }

    } else NotFoundScreen(
        onBackClick = onBackClick,
        title = "Log Information",
        message = "User data not found"
    )
}

@Composable
private fun NutrientSection(
    title: String,
    nutrients: List<NutrientDataWithAmount>,
    isBasicNutrient: Boolean,
    isUserPremium: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.areaContainer(),
        content = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            PagedNutrients(
                nutrients = nutrients,
                displayFormat = UNutrient.ScrollableNutrientsFormat.BOX,
                isDataMinimal = true,
                isBasicNutrient = isBasicNutrient,
                isBaseSizeDisplay = false,
                isUserPremium = isUserPremium
            )
        }
    )
}

private data class NutrientSectionConfig(
    val title: String,
    val nutrients: List<NutrientDataWithAmount>,
    val isBasicNutrient: Boolean = true,
    val shouldShow: Boolean = true,
)

private fun getNutrientSectionsConfig(nutrients: NutrientsByType<NutrientDataWithAmount>): List<NutrientSectionConfig> {

    return listOf(
        NutrientSectionConfig(
            title = "Summary",
            nutrients = nutrients.basic,
        ),
        NutrientSectionConfig(
            title = "Vitamins",
            nutrients = nutrients.vitamin,
            isBasicNutrient = false,
            shouldShow = nutrients.vitamin.isNotEmpty(),
        ),
        NutrientSectionConfig(
            title = "Minerals",
            nutrients = nutrients.mineral,
            isBasicNutrient = false,
            shouldShow = nutrients.mineral.isNotEmpty()
        )
    )
}