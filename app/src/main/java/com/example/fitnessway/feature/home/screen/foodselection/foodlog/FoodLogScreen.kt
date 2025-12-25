package com.example.fitnessway.feature.home.screen.foodselection.foodlog

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.feature.home.screen.foodselection.foodlog.composables.FoodLogInformationList
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.HeaderRow
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Food.Ui.getFoodLogCategory
import com.example.fitnessway.util.Food.calcNutrientIntakesFromFoodLogServings
import com.example.fitnessway.util.Nutrient
import com.example.fitnessway.util.Nutrient.Ui.PagedNutrients
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.Ui.AppLabel
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodLogFieldsProvider
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodLogScreen(
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val user = viewModel.user
    val foodLogFormState by viewModel.foodLogFormState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val foodLogAddState = uiState.foodLogAddState

    val foodLogCategory by viewModel.foodLogCategory.collectAsState()
    val selectedFoodToLog by viewModel.selectedFoodToLog.collectAsState()
    val time = viewModel.getCurrentTime()

    var shouldShowFoodLogSuccess by remember { mutableStateOf(false) }

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

    LaunchedEffect(foodLogAddState) {
        if (foodLogAddState is UiState.Success) {
            shouldShowFoodLogSuccess = true
            delay(5000)
            shouldShowFoodLogSuccess = false
            viewModel.resetFoodLogAddState()
        }
    }

    val foodLogCategoryCopy = foodLogCategory
    val foodLogFormStateCopy = foodLogFormState
    val selectedFoodToLogCopy = selectedFoodToLog

    if (user != null) {
        if (foodLogCategoryCopy != null &&
            foodLogFormStateCopy != null &&
            selectedFoodToLogCopy != null
        ) {
            val foodLogCategoryString = getFoodLogCategory(foodLogCategoryCopy)

            Screen(
                header = {
                    Header(
                        onBackClick = onBackClick,
                        isOnBackEnabled = foodLogAddState !is UiState.Loading,
                        title = "Log Submission"
                    ) {
                        HeaderRow {
                            AppLabel(
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
                }
            ) {
                val fieldsProvider = FoodLogFieldsProvider(
                    formState = foodLogFormStateCopy,
                    onFieldUpdate = { fieldName, value ->
                        viewModel.updateFoodLogFormField(fieldName, value)
                    }
                )

                CompositionLocalProvider(
                    values = arrayOf(LocalOverscrollFactory provides null),
                    content = {
                        val nutrients = remember(
                            selectedFoodToLogCopy.nutrients,
                            foodLogFormStateCopy.data.servings
                        ) {
                            val servings =
                                foodLogFormStateCopy.data.servings.toDoubleOrNull() ?: 0.0

                            calcNutrientIntakesFromFoodLogServings(
                                nutrients = selectedFoodToLogCopy.nutrients,
                                currentServings = 1.0,
                                newServings = servings
                            )
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            content = {
                                if (foodLogAddState is UiState.Error) {
                                    item {
                                        ApiErrorMessage(foodLogAddState.message)
                                    }
                                }

                                item {
                                    FoodLogInformationList(
                                        food = selectedFoodToLogCopy,
                                        servingsField = fieldsProvider.servings(),
                                        amountPerServingsField = fieldsProvider.amountPerServing(
                                            servingUnit = selectedFoodToLogCopy.information.servingUnit
                                        ),
                                        timeField = fieldsProvider.time()
                                    )
                                }

                                val sections = getNutrientSectionsConfig(nutrients)

                                sections.forEach { section ->
                                    if (section.shouldShow) {
                                        item {
                                            NutrientSection(
                                                title = section.title,
                                                nutrients = section.nutrients,
                                                isUserPremium = user.isPremium,
                                                isBasicNutrient = section.isBasicNutrient
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                )
            }
        }

    } else Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                title = "Log Information"
            )
        }
    ) { NotFoundText("User data not found") }
}

@Composable
private fun NutrientSection(
    title: String,
    nutrients: List<NutrientAmountData>,
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
                displayFormat = Nutrient.ScrollableNutrientsFormat.BOX,
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
    val nutrients: List<NutrientAmountData>,
    val isBasicNutrient: Boolean = true,
    val shouldShow: Boolean = true,
)

private fun getNutrientSectionsConfig(nutrients: NutrientsByType<NutrientAmountData>): List<NutrientSectionConfig> {

    return listOf(
        NutrientSectionConfig(
            title = "Nutrient Summary",
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