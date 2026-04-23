package com.example.fitnessway.feature.home.screen.foodselection.foodlog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toPascalSpaced
import com.example.fitnessway.data.mappers.toTypedList
import com.example.fitnessway.data.model.m_26.FoodInformationWithId
import com.example.fitnessway.data.model.m_26.FoodSource
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.feature.home.screen.foodselection.foodlog.composables.FoodLogInformation
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.nutrient.NutrientsViewFormat
import com.example.fitnessway.ui.nutrient.PagedNutrients
import com.example.fitnessway.ui.shared.Banners
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.Ui.AppLabel
import com.example.fitnessway.util.Ui.handleApiSuccessTempState
import com.example.fitnessway.util.Ui.handleTempApiErrMsg
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.extensions.calcFoodLogNutrients
import com.example.fitnessway.util.form.field.provider.FoodLogFieldsProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodLogScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onPopBackStack: () -> Unit
) {
    val user by viewModel.userFlow.collectAsState()

    val uiState by viewModel.uiState.collectAsState()
    val appFoodRepoUiState by viewModel.appFoodRepoUiState.collectAsState()
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()

    val searchCriteria = viewModel.searchCriteria.collectAsState().value
    val foodToLog = viewModel.foodToLog.collectAsState().value
    val formState = viewModel.foodLogFormState.collectAsState().value
    val category = viewModel.foodLogCategory.collectAsState().value

    val appFoodUiState = appFoodRepoUiState.appFood
    val foodsUiState = foodRepoUiState.uiStatePager
    val foodLogAddState = uiState.foodLogAddState

    val isLogSuccessFull = handleApiSuccessTempState(
        uiState = foodLogAddState,
        onTimeout = viewModel::resetFoodLogAddState
    )

    val logErrorMessage = handleTempApiErrMsg(
        uiState = foodLogAddState,
        onTimeOut = viewModel::resetFoodLogAddState
    )

    fun onBackClick() {
        viewModel.resetFoodLogAddState()
        if (foodLogAddState.hasState) viewModel.resetFoodLogAddState()
        onPopBackStack()
    }

    BackHandler { onBackClick() }

    LaunchedEffect(searchCriteria) {
        searchCriteria?.let {
            when (it.source) {
                FoodSource.APP -> viewModel.getAppFoodById(searchCriteria.id)
                FoodSource.USER -> viewModel.getFoods()
            }
        }
    }

    LaunchedEffect(appFoodUiState, searchCriteria) {
        if (appFoodUiState is UiState.Success &&
            searchCriteria?.source == FoodSource.APP
        ) {
            appFoodUiState.data?.let {
                viewModel.setFoodToLog(
                    FoodInformationWithId(
                        id = it.id,
                        information = it.information
                    )
                )
            }
        }
    }

    LaunchedEffect(foodsUiState, searchCriteria) {
        if (foodsUiState.uiState is UiState.Success &&
            searchCriteria?.source == FoodSource.USER
        ) {
            foodsUiState.uiState.data.data
                .find { it.id == searchCriteria.id }
                ?.let {
                    viewModel.setFoodToLog(
                        foodToLog = FoodInformationWithId(
                            id = it.id,
                            information = it.information
                        )
                    )
                }
        }
    }

    LaunchedEffect(foodToLog) {
        foodToLog?.let {
            viewModel.initializeFoodLogForm(
                food = it.information,
                time = viewModel.dateTimeFormatter.getCurrentTime()
            )
        }
    }

    val isScreenDataReady = searchCriteria != null &&
            foodToLog != null &&
            formState != null &&
            category != null

    if (isScreenDataReady) {
        Screen(
            header = {
                Header(
                    onBackClick = ::onBackClick,
                    isOnBackEnabled = foodLogAddState !is UiState.Loading,
                    title = "Log Submission"
                ) {
                    AppLabel<Unit>(
                        text = category.name.toPascalSpaced(),
                        size = Ui.LabelSize.MEDIUM,
                    )

                    Clickables.HeaderDoneButton(
                        onClick = viewModel::addFoodLog,
                        enabled = viewModel.isFoodLogFormValid,
                        isLoading = foodLogAddState is UiState.Loading
                    )
                }
            }
        ) { focusManager ->
            val fieldsProvider = FoodLogFieldsProvider(
                formState = formState,
                focusManager = focusManager,
                isFormSubmitting = foodLogAddState is UiState.Loading,
                onFieldUpdate = { fieldName, value ->
                    viewModel.updateFoodLogFormField(fieldName, value)
                }
            )

            val foodNutrients = remember(
                foodToLog.information.nutrients,
                formState.data.servings
            ) {
                foodToLog.information.nutrients.calcFoodLogNutrients(
                    currentServings = 1.0,
                    newServings = formState.data.servings.toDoubleOrNull() ?: 0.0
                )
            }

            Box(Modifier.fillMaxSize()) {
                val isAppFoodLoading = searchCriteria.source == FoodSource.APP && appFoodUiState is UiState.Loading
                val isUserFoodLoading = searchCriteria.source == FoodSource.USER && foodsUiState is UiState.Loading

                if (isAppFoodLoading || isUserFoodLoading) {
                    Loading.SpinnerInScreen()
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Banners.ErrorBannerAnimated(
                            isVisible = logErrorMessage != null,
                            text = logErrorMessage ?: ""
                        )

                        FoodLogInformation(
                            food = foodToLog.information,
                            servingsField = fieldsProvider.servings(),
                            amountPerServingsField = fieldsProvider.amountPerServing(
                                servingUnit = foodToLog.information.base.servingUnit.name.lowercase()
                            ),
                            timeField = fieldsProvider.time()
                        )

                        foodNutrients.toTypedList().forEach { (type, nutrientsInFood) ->
                            if (nutrientsInFood.isNotEmpty()) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .areaContainer(
                                            borderColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                ) {
                                    Text(
                                        text = type.name.toPascalSpaced(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    PagedNutrients(
                                        nutrients = nutrientsInFood,
                                        viewFormat = NutrientsViewFormat.BOX,
                                        isDataMinimal = true,
                                        isBasicNutrient = type == NutrientType.BASIC,
                                        isBaseSizeDisplay = false,
                                        isUserPremium = user?.isPremium ?: false
                                    )
                                }
                            }
                        }
                    }
                }

                Banners.SuccessBannerAnimated(
                    text = "Food logged successfully",
                    isVisible = isLogSuccessFull,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}