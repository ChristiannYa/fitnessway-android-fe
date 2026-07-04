package com.example.fitnessway.feature.home.screen.foodselection.foodlog

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.mappers.toSuccessOrNull
import com.example.fitnessway.data.mappers.toTitleCase
import com.example.fitnessway.data.mappers.toTypedList
import com.example.fitnessway.data.model.m_26.AppEdibleReport
import com.example.fitnessway.data.model.m_26.AppEdibleReportRequest
import com.example.fitnessway.data.model.m_26.EdibleInformation
import com.example.fitnessway.data.model.m_26.EdibleScope
import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.FoodInformationWithId
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.feature.home.screen.foodselection.foodlog.composables.FoodLogInformation
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.edible.AppEdibleReportOptionsPopup
import com.example.fitnessway.ui.nutrient.NutrientsViewFormat
import com.example.fitnessway.ui.nutrient.PagedNutrients
import com.example.fitnessway.ui.shared.Banners
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.DarkOverlay
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.Ui.AppLabel
import com.example.fitnessway.util.Ui.handleApiSuccessTempState
import com.example.fitnessway.util.Ui.handleTempApiErrMsg
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.extensions.calcFoodLogNutrients
import com.example.fitnessway.util.form.field.provider.FoodLogFieldsProvider
import com.example.fitnessway.util.toEnum
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodLogScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onPopBackStack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userRepoUiState by viewModel.userRepoUiState.collectAsState()
    val appFoodRepoUiState by viewModel.appFoodRepoUiState.collectAsState()
    val userFoodRepoUiState by viewModel.userFoodRepoUiState.collectAsState()
    val userSupplementRepoUiState by viewModel.userSupplementRepoUiState.collectAsState()
    val nutrientsRepoUiState by viewModel.nutrientRepoUiState.collectAsState()

    val searchCriteria = viewModel.searchCriteria.collectAsState().value
    val foodToLog = viewModel.foodToLog.collectAsState().value
    val formState = viewModel.foodLogFormState.collectAsState().value
    val category = viewModel.foodLogCategory.collectAsState().value

    val user = userRepoUiState.userUiState.toSuccessOrNull()
    val appFoodUiState = appFoodRepoUiState.appEdible
    val userFoodsUiState = userFoodRepoUiState.uiStatePager
    val userSupplementsUiState = userSupplementRepoUiState.uiStatePager
    val nutrientsUiState = nutrientsRepoUiState.nutrients

    val foodLogAddState = uiState.foodLogAddState

    val isAppEdibleLoading = searchCriteria?.source == EdibleSource.APP && appFoodUiState is UiState.Loading

    val isLogSuccessFull = handleApiSuccessTempState(
        uiState = foodLogAddState,
        onTimeout = viewModel::resetFoodLogAddState
    )

    val logErrorMessage = handleTempApiErrMsg(
        uiState = foodLogAddState,
        onTimeOut = viewModel::resetFoodLogAddState
    )

    var isReportOptionsVisible by remember { mutableStateOf(false) }
    var isReportConfirmed by remember { mutableStateOf(false) }
    var finalReport by remember { mutableStateOf<AppEdibleReportRequest?>(null) }

    fun onBackClick() {
        viewModel.resetFoodLogAddState()
        onPopBackStack()
    }

    BackHandler { onBackClick() }

    LaunchedEffect(searchCriteria) {
        searchCriteria?.let {
            when (it.source) {
                EdibleSource.APP -> {
                    when (it.scope) {
                        is EdibleScope.Id -> viewModel.getAppEdibleById(it.scope.id)
                        is EdibleScope.Barcode -> viewModel.getAppEdibleByBarcode(it.scope.barcode)
                    }
                }

                EdibleSource.USER -> {
                    if (it.edibleType == EdibleType.FOOD) {
                        viewModel.getUserFoods()
                    } else viewModel.getUserSupplements()
                }
            }
        }
    }

    LaunchedEffect(appFoodUiState, searchCriteria) {
        if (appFoodUiState is UiState.Success &&
            searchCriteria?.source == EdibleSource.APP
        ) {
            appFoodUiState.data?.let {
                viewModel.setFoodToLog(
                    FoodInformationWithId(
                        id = it.edible.id,
                        information = it.edible.information
                    )
                )
            }
        }
    }

    LaunchedEffect(userFoodsUiState, searchCriteria) {
        if (userFoodsUiState.uiState is UiState.Success &&
            searchCriteria?.source == EdibleSource.USER &&
            searchCriteria.edibleType == EdibleType.FOOD &&
            searchCriteria.scope is EdibleScope.Id
        ) {
            userFoodsUiState.uiState.data.data
                .find { it.id == searchCriteria.scope.id }
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

    LaunchedEffect(userSupplementsUiState, searchCriteria) {
        if (userSupplementsUiState.uiState is UiState.Success &&
            searchCriteria?.source == EdibleSource.USER &&
            searchCriteria.edibleType == EdibleType.SUPPLEMENT &&
            searchCriteria.scope is EdibleScope.Id
        ) {
            userSupplementsUiState.uiState.data.data
                .find { it.id == searchCriteria.scope.id }
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

    Screen(
        header = {
            Header(
                onBackClick = ::onBackClick,
                isOnBackEnabled = foodLogAddState !is UiState.Loading,
                title = "Log Submission"
            ) {
                AppLabel<Unit>(
                    text = category?.name?.toTitleCase() ?: "",
                    size = Ui.LabelSize.MEDIUM,
                )

                Clickables.DoneButton(
                    onClick = viewModel::addFoodLog,
                    enabled = !isAppEdibleLoading &&
                            viewModel.isFoodLogFormValid &&
                            !isReportOptionsVisible &&
                            !isReportConfirmed &&
                            finalReport == null,
                    isLoading = foodLogAddState is UiState.Loading
                )
            }
        }
    ) { focusManager ->
        if (isAppEdibleLoading) Loading.SpinnerInScreen(Modifier.padding(top = 16.dp))
        else if (!(formState == null || foodToLog == null || searchCriteria == null)) {
            Box(Modifier.fillMaxSize()) {
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

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
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
                                    text = type.name.toTitleCase(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

                                PagedNutrients(
                                    nutrients = nutrientsInFood,
                                    apiNutrients = nutrientsUiState.toSuccessOrNull()?.toList() ?: emptyList(),
                                    viewFormat = NutrientsViewFormat.BOX,
                                    isDataMinimal = true,
                                    isBasicNutrient = type == NutrientType.BASIC,
                                    isBaseSizeDisplay = false,
                                    isUserPremium = user?.isPremium ?: false
                                )
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))
                    if (searchCriteria.source == EdibleSource.APP) {
                        TextButton(
                            onClick = { isReportOptionsVisible = true }
                        ) {
                            Text("Something is wrong")
                        }
                    }
                }

                DarkOverlay(
                    isVisible = isReportOptionsVisible || isReportConfirmed || finalReport != null,
                    onClick = {
                        when {
                            isReportOptionsVisible -> isReportOptionsVisible = false
                            isReportConfirmed -> isReportConfirmed = false
                            finalReport != null -> {
                                finalReport = null
                                isReportOptionsVisible = true
                            }
                        }
                    }
                )

                AppEdibleReportOptionsPopup(
                    edible = foodToLog,
                    isVisible = isReportOptionsVisible,
                    isReportConfirmed = isReportConfirmed,
                    onReport = {
                        finalReport = it
                        isReportOptionsVisible = false
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )

                AppEdibleReportConfirmationPopup(
                    finalReport = finalReport,
                    edibleInfo = foodToLog.information,
                    onConfirmReport = {
                        finalReport?.let { viewModel.reportAppEdible(it) }
                        if (finalReport != null) {
                            finalReport = null
                            isReportConfirmed = true
                        }
                    },
                    modifier = Modifier.align(Alignment.Center)
                )

                AnimatedVisibility(
                    visible = isReportConfirmed,
                    enter = Animation.ComposableTransition.ScaleWithSpring.enter(),
                    exit = Animation.ComposableTransition.ScaleWithSpring.exit(),
                    modifier = Modifier
                        .width(Ui.Measurements.POP_UP_CONTAINER_WIDTH)
                        .align(Alignment.Center)
                ) {
                    Box(
                        Modifier.areaContainer(
                            borderColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append("Report submitted\n")
                                }

                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                                    )
                                ) {
                                    append("Thank you! We appreciate you taking the time to help us improve the app")
                                }
                            }
                        )
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

@Composable
private fun AppEdibleReportConfirmationPopup(
    finalReport: AppEdibleReportRequest?,
    edibleInfo: EdibleInformation,
    onConfirmReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = finalReport != null,
        enter = Animation.ComposableTransition.ScaleWithSpring.enter(),
        exit = Animation.ComposableTransition.ScaleWithSpring.exit(),
        modifier = modifier
    ) {
        Box(
            Modifier
                .areaContainer(
                    borderColor = MaterialTheme.colorScheme.surfaceVariant
                )
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("Please review your report\n")
                        }

                        withStyle(
                            style = SpanStyle(
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
                            )
                        ) {
                            append(edibleInfo.base.name)
                        }
                    }
                )

                val reasonsHorizontalScrollState = rememberScrollState()
                Text(
                    text = buildString {
                        append("Reasons\n")

                        finalReport?.reasons?.forEach { reason ->
                            val reasonDisplay = with(reason.toTitleCase()) {
                                if (reason.toEnum<AppEdibleReport.Reason>() == AppEdibleReport.Reason.INCORRECT_TYPE) {
                                    "$this (Current: ${edibleInfo.type.toString().toTitleCase()})"
                                } else this
                            }

                            appendLine("  - $reasonDisplay")
                        }

                        if (finalReport?.notes != null) appendLine("\n${finalReport.notes}")
                    }.trimEnd(),
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.horizontalScroll(reasonsHorizontalScrollState)
                )

                TextButton(
                    onClick = onConfirmReport,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = WhiteFont
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Report",
                        fontFamily = robotoSerifFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
