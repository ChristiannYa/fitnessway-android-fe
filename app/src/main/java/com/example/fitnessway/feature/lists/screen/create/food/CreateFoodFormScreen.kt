package com.example.fitnessway.feature.lists.screen.create.food

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.feature.lists.screen.create.food.composables.FormProgressIndicator
import com.example.fitnessway.feature.lists.screen.create.food.composables.NextButton
import com.example.fitnessway.feature.lists.screen.create.food.composables.SetBasicData
import com.example.fitnessway.feature.lists.screen.create.food.composables.SetNutrients
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.shared.ApiErrorMessageAnimated
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Loading.LoadingArea
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.SuccessIcon
import com.example.fitnessway.util.Nutrient.filterNutrientsByType
import com.example.fitnessway.util.Nutrient.filterOutNutrientsWithGoal
import com.example.fitnessway.util.Nutrient.filterOutNutrientsWithoutGoal
import com.example.fitnessway.util.Nutrient.filterOutPremiumNutrients
import com.example.fitnessway.util.Ui.handleErrStateTempMsg
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodCreationFieldsProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateFoodFormScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()
    val foodCreationFormState by viewModel.foodCreationFormState.collectAsState()

    val focusManager = LocalFocusManager.current
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState

    val fieldsProvider = FoodCreationFieldsProvider(
        formState = foodCreationFormState,
        onFieldUpdate = { fieldName, value ->
            viewModel.updateFoodCreationFormField(fieldName, value)
        },
        focusManager = focusManager
    )

    val finalTitle = if (uiState.foodAddState is UiState.Success) {
        "Go home"
    } else "Minerals"

    val (title, nextButtonText) = when (currentStep) {
        1 -> "Food Information" to "Add Nutrients"
        2 -> "Nutrients" to "Add Vitamins"
        3 -> "Vitamins" to "Add Minerals"
        4 -> finalTitle to "Create Food"
        else -> "" to ""
    }

    val foodAddErrMsg = handleErrStateTempMsg(
        uiState = uiState.foodAddState,
        onTimeOut = viewModel::resetFoodAddState
    )

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.getNutrients()
    }

    if (viewModel.user != null) {
        Screen(
            header = {
                Header(
                    onBackClick = {
                        if (uiState.foodAddState is UiState.Success) {
                            onBackClick()

                            scope.launch {
                                // Wait for the transition to the previous screen
                                delay(500)

                                viewModel.resetFoodFormState()
                                viewModel.resetFoodAddState()
                            }
                        } else {
                            if (uiState.foodAddState !is UiState.Loading) {
                                viewModel.updateStep(
                                    step = currentStep,
                                    goesBack = true,
                                    onExitForm = onBackClick
                                )
                            }
                        }
                    },
                    isOnBackEnabled = uiState.foodAddState !is UiState.Loading,
                    title = title
                )
            }
        ) {
            when (nutrientsUiState) {
                is UiState.Loading -> LoadingArea()

                is UiState.Success -> {
                    if (uiState.foodAddState is UiState.Success) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SuccessIcon()
                            Text(
                                text = "Food Added Successfully!",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxHeight()
                                .imePadding(),
                        ) {
                            val isUserPremium = viewModel.user.isPremium
                            val nutrients = nutrientsUiState.data

                            val foodBaseFields = listOf(
                                fieldsProvider.name(),
                                fieldsProvider.brand(),
                                fieldsProvider.amountPerServing(),
                                fieldsProvider.servingUnit(
                                    errorMessage = viewModel.createFormServingUnitError
                                ),
                            )

                            val nutrientFieldsData = NutrientType.entries
                                .associateWith { type ->
                                    val nutrientsByType = filterNutrientsByType(
                                        nutrients = nutrients,
                                        type = type
                                    )
                                        .filterOutPremiumNutrients(isUserPremium)

                                    val nutrientsWithGoal = nutrientsByType
                                        .filterOutNutrientsWithoutGoal()

                                    val fields = nutrientsWithGoal
                                        .mapIndexed { index, nutrientWithPrefs ->
                                            fieldsProvider.nutrient(
                                                nutrientWithPreferences = nutrientWithPrefs,
                                                isLastField = index == nutrientsWithGoal.lastIndex
                                            )
                                        }

                                    val withoutGoal = nutrientsByType
                                        .filterOutNutrientsWithGoal()
                                        .map { it.nutrient }

                                    Pair(fields, withoutGoal)
                                }

                            val areNsValid = viewModel.areBasicNutrientsValid && currentStep >= 2

                            val areVsValid = viewModel.validateFoodNonBaseNutrients(
                                nutrients = nutrients.vitamin.map { it.nutrient }
                            ) && currentStep >= 3

                            val areMsValid = viewModel.validateFoodNonBaseNutrients(
                                nutrients = nutrients.mineral.map { it.nutrient }
                            ) && currentStep >= 4

                            val isCurrentStepValid = when (currentStep) {
                                1 -> viewModel.isBasicDataValid
                                2 -> areNsValid
                                3 -> areVsValid
                                4 -> areMsValid
                                else -> false
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                ApiErrorMessageAnimated(
                                    isVisible = foodAddErrMsg != "",
                                    errorMessage = foodAddErrMsg
                                )

                                FormProgressIndicator(
                                    currentStep = currentStep,
                                    isStepOneValid = viewModel.isBasicDataValid,
                                    isStepTwoValid = areNsValid,
                                    isStepThreeValid = areVsValid,
                                    isStepFourValid = areMsValid
                                )

                                Column {
                                    AnimatedContent(
                                        targetState = currentStep,
                                        transitionSpec = {
                                            val isForward = targetState > initialState

                                            if (isForward)
                                                slideInHorizontally { it } + fadeIn() + scaleIn(
                                                    initialScale = 0.7f
                                                ) togetherWith
                                                        slideOutHorizontally { -it } + fadeOut() + scaleOut(
                                                    targetScale = 0.7f
                                                )
                                            else
                                                slideInHorizontally { -it } + fadeIn() + scaleIn(
                                                    initialScale = 0.7f
                                                ) togetherWith
                                                        slideOutHorizontally { it } + fadeOut() + scaleOut(
                                                    targetScale = 0.7f
                                                )
                                        }
                                    ) { step ->
                                        val nutrientFields =
                                            nutrientFieldsData[NutrientType.BASIC]?.first.orEmpty()
                                        val nutrientsWithoutGoal =
                                            nutrientFieldsData[NutrientType.BASIC]?.second.orEmpty()

                                        val vitaminFields =
                                            nutrientFieldsData[NutrientType.VITAMIN]?.first.orEmpty()
                                        val vitaminsWithoutGoal =
                                            nutrientFieldsData[NutrientType.VITAMIN]?.second.orEmpty()

                                        val mineralFields =
                                            nutrientFieldsData[NutrientType.MINERAL]?.first.orEmpty()
                                        val mineralsWithoutGoal =
                                            nutrientFieldsData[NutrientType.MINERAL]?.second.orEmpty()

                                        when (step) {
                                            1 -> SetBasicData(foodBaseFields)

                                            2 -> SetNutrients(
                                                fields = nutrientFields,
                                                nutrientsWithoutGoal = nutrientsWithoutGoal
                                            )

                                            3 -> SetNutrients(
                                                fields = vitaminFields,
                                                nutrientsWithoutGoal = vitaminsWithoutGoal
                                            )

                                            4 -> SetNutrients(
                                                fields = mineralFields,
                                                nutrientsWithoutGoal = mineralsWithoutGoal
                                            )
                                        }
                                    }
                                }
                            }

                            NextButton(
                                onClick = {
                                    viewModel.updateStep(
                                        step = currentStep,
                                        goesBack = false,
                                        onSubmit = viewModel::addFood
                                    )
                                },
                                enabled = isCurrentStepValid,
                                isSubmitting = uiState.foodAddState is UiState.Loading,
                                text = nextButtonText
                            )
                        }
                    }
                }

                is UiState.Error -> ApiErrorMessage(
                    errMsg = nutrientsUiState.message
                )

                is UiState.Idle -> {}
            }
        }
    }
}