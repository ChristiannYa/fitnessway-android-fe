package com.example.fitnessway.feature.lists.screen.create.food

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MNutrient.Enum.NutrientType
import com.example.fitnessway.feature.lists.screen.create.food.composables.FormProgressIndicator
import com.example.fitnessway.feature.lists.screen.create.food.composables.NextButton
import com.example.fitnessway.feature.lists.screen.create.food.composables.SetBasicData
import com.example.fitnessway.feature.lists.screen.create.food.composables.SetNutrients
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Messages.SuccessMessageAnimated
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.UNutrient.filterNutrientsByType
import com.example.fitnessway.util.UNutrient.filterOutNutrientsWithGoal
import com.example.fitnessway.util.UNutrient.filterOutNutrientsWithoutGoal
import com.example.fitnessway.util.UNutrient.filterOutPremiumNutrients
import com.example.fitnessway.util.UNutrient.getIds
import com.example.fitnessway.util.Ui.handleTempApiErrorMessage
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodCreationFieldsProvider
import com.example.fitnessway.util.isLoading
import com.example.fitnessway.util.isSuccess
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateFoodFormScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()
    val foodCreationFormState by viewModel.foodCreationFormState.collectAsState()

    val user = userFlow
    val foodAddState = uiState.foodAddState
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState
    val nutrientDvControls = viewModel.nutrientDvControls

    val finalTitle = if (foodAddState is UiState.Success) {
        "Go home"
    } else "Minerals"

    val (title, nextButtonText) = when (currentStep) {
        1 -> "Food Information" to "Add Nutrients"
        2 -> "Nutrients" to "Add Vitamins"
        3 -> "Vitamins" to "Add Minerals"
        4 -> finalTitle to "Create Food"
        else -> "" to ""
    }

    val foodAddErrMsg = handleTempApiErrorMessage(
        uiState = foodAddState,
        onTimeOut = viewModel::resetFoodAddState
    )

    val focusManager = LocalFocusManager.current
    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterNutrient = remember { FocusRequester() }
    val focusRequesterVitamin = remember { FocusRequester() }
    val focusRequesterMineral = remember { FocusRequester() }

    val fieldsProvider = FoodCreationFieldsProvider(
        formState = foodCreationFormState,
        isFormSubmitting = foodAddState is UiState.Loading,
        onFieldUpdate = { fieldName, value ->
            viewModel.updateFoodCreationFormField(fieldName, value)
        },
        focusManager = focusManager
    )

    LaunchedEffect(currentStep) {
        val focusRequester = when (currentStep) {
            1 -> focusRequesterName
            2 -> focusRequesterNutrient
            3 -> focusRequesterVitamin
            4 -> focusRequesterMineral
            else -> null
        }

        focusRequester?.let {
            delay(200)
            it.requestFocus()
        }
    }

    if (user != null && nutrientsUiState is UiState.Success) {
        val nutrients = nutrientsUiState.data

        val areNsValid = viewModel.validateRequiredNutrients(
            nutrientIds = nutrients.basic.map { it.nutrient }.getIds().toSet()
        ) && currentStep >= 2

        val areVsValid = viewModel.validateOptionalNutrients(
            nutrientIds = nutrients.vitamin.map { it.nutrient }.getIds().toSet()
        ) && currentStep >= 3

        val areMsValid = viewModel.validateOptionalNutrients(
            nutrientIds = nutrients.mineral.map { it.nutrient }.getIds().toSet()
        ) && currentStep >= 4

        Screen(
            header = {
                Header(
                    onBackClick = {
                        val onScreenLeave = {
                            onBackClick()
                            viewModel.resetFoodCreationStates()
                        }

                        val goBackOnFoodAddIdleState = {
                            if (currentStep == 1) onScreenLeave() else {
                                viewModel.updateStep(currentStep, true)
                            }
                        }

                        when (foodAddState) {
                            is UiState.Success -> onScreenLeave()
                            is UiState.Error -> goBackOnFoodAddIdleState()
                            is UiState.Idle -> goBackOnFoodAddIdleState()
                            else -> {}
                        }
                    },
                    isOnBackEnabled = !foodAddState.isLoading,
                    title = title,
                    extraContent = if (!foodAddState.isSuccess) {
                        {
                            FormProgressIndicator(
                                currentStep = currentStep,
                                isStepOneValid = viewModel.isBasicDataValid,
                                isStepTwoValid = areNsValid,
                                isStepThreeValid = areVsValid,
                                isStepFourValid = areMsValid
                            )
                        }
                    } else null
                )
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SuccessMessageAnimated(
                    isVisible = foodAddState.isSuccess,
                    message = "Food added successfully!"
                )

                AnimatedVisibility(
                    visible = !foodAddState.isSuccess,
                    enter = Animation.ComposableTransition.fadeIn,
                    exit = Animation.ComposableTransition.fadeOut
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxHeight()
                            .imePadding(),
                    ) {
                        val isUserPremium = user.isPremium

                        val foodBaseFields = listOf(
                            fieldsProvider.name(
                                focusRequester = focusRequesterName
                            ),
                            fieldsProvider.brand(),
                            fieldsProvider.amountPerServing(),
                            fieldsProvider.servingUnit(
                                errorMessage = viewModel.createFormServingUnitError
                            ),
                        )

                        val nutrientFieldsData = NutrientType.entries.associateWith { type ->
                            val nutrientsByType = filterNutrientsByType(
                                nutrients = nutrients,
                                type = type
                            )
                                .filterOutPremiumNutrients(isUserPremium)

                            val nutrientsWithGoal = nutrientsByType
                                .filterOutNutrientsWithoutGoal()

                            val fields = nutrientsWithGoal
                                .mapIndexed { index, nutrientWithPrefs ->
                                    // Create focus requester only if the index is 0
                                    val focusRequester = if (index == 0) {
                                        when (type) {
                                            NutrientType.BASIC -> focusRequesterNutrient
                                            NutrientType.VITAMIN -> focusRequesterVitamin
                                            NutrientType.MINERAL -> focusRequesterMineral
                                        }
                                    } else null

                                    fieldsProvider.nutrient(
                                        nutrientWithPreferences = nutrientWithPrefs,
                                        focusRequester = focusRequester,
                                        isLastField = index == nutrientsWithGoal.lastIndex
                                    )
                                }

                            val withoutGoal = nutrientsByType
                                .filterOutNutrientsWithGoal()
                                .map { it.nutrient }

                            Pair(fields, withoutGoal)
                        }

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
                            ErrorBannerAnimated(
                                isVisible = foodAddErrMsg != null,
                                text = foodAddErrMsg ?: ""
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
                                        nutrientFieldsData[NutrientType.BASIC]
                                            ?.first.orEmpty()
                                    val nutrientsWithoutGoal = Pair(
                                        NutrientType.BASIC,
                                        nutrientFieldsData[NutrientType.BASIC]
                                            ?.second.orEmpty()
                                    )

                                    val vitaminFields =
                                        nutrientFieldsData[NutrientType.VITAMIN]
                                            ?.first.orEmpty()
                                    val vitaminsWithoutGoal = Pair(
                                        NutrientType.VITAMIN,
                                        nutrientFieldsData[NutrientType.VITAMIN]
                                            ?.second.orEmpty()
                                    )

                                    val mineralFields =
                                        nutrientFieldsData[NutrientType.MINERAL]
                                            ?.first.orEmpty()
                                    val mineralsWithoutGoal = Pair(
                                        NutrientType.MINERAL,
                                        nutrientFieldsData[NutrientType.MINERAL]
                                            ?.second.orEmpty()
                                    )

                                    when (step) {
                                        1 -> SetBasicData(foodBaseFields)

                                        2 -> SetNutrients(
                                            fields = nutrientFields,
                                            nutrientsWithoutGoal = nutrientsWithoutGoal,
                                            nutrientDvControls = nutrientDvControls
                                        )

                                        3 -> SetNutrients(
                                            fields = vitaminFields,
                                            nutrientsWithoutGoal = vitaminsWithoutGoal,
                                            nutrientDvControls = nutrientDvControls
                                        )

                                        4 -> SetNutrients(
                                            fields = mineralFields,
                                            nutrientsWithoutGoal = mineralsWithoutGoal,
                                            nutrientDvControls = nutrientDvControls
                                        )
                                    }
                                }
                            }
                        }

                        NextButton(
                            enabled = isCurrentStepValid,
                            isSubmitting = foodAddState.isLoading,
                            text = nextButtonText
                        ) {
                            viewModel.updateStep(
                                step = currentStep,
                                goesBack = false
                            ) {
                                focusManager.clearFocus()
                                viewModel.addFood()
                            }
                        }
                    }
                }
            }
        }
    }
}