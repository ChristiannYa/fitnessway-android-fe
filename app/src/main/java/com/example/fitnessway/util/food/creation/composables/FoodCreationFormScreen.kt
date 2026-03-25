package com.example.fitnessway.util.food.creation.composables

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toErrorMessageOrNull
import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.model.m_26.FoodSource
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.ui.shared.Banners
import com.example.fitnessway.ui.shared.FormProgress
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Messages
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.UNutrient.filterGoalNotSet
import com.example.fitnessway.util.UNutrient.filterGoalSetPreferences
import com.example.fitnessway.util.UNutrient.filterNonPremiumPreferences
import com.example.fitnessway.util.UNutrient.filterNutrientsByType
import com.example.fitnessway.util.UNutrient.getIds
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.food.creation.IFoodCreation
import com.example.fitnessway.util.form.field.provider.FoodCreationFieldsProvider
import kotlinx.coroutines.delay

@Composable
fun <T> FoodCreationFormScreen(
    onBackClick: () -> Unit,
    foodCreation: IFoodCreation,
    foodSource: FoodSource,
    nutrientsUiState: UiState<MNutrient.Model.NutrientsByType<MNutrient.Model.NutrientWithPreferences>>,
    isUserPremium: Boolean,
    onResetSubmissionState: () -> Unit,
    submissionState: UiState<T>,
    submissionErrorMessage: String?,
    onSubmit: () -> Unit,
) {
    val formState by foodCreation.formState.collectAsState()
    val currentStep by foodCreation.currentStep.collectAsState()

    val focusManager = LocalFocusManager.current
    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterNutrient = remember { FocusRequester() }
    val focusRequesterVitamin = remember { FocusRequester() }
    val focusRequesterMineral = remember { FocusRequester() }

    val fieldsProvider = FoodCreationFieldsProvider(
        formState = formState,
        onFieldUpdate = foodCreation::updateFormField,
        focusManager = focusManager,
        isFormSubmitting = submissionState is UiState.Loading
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

    val (stepTitle, nextButtonText) = run {
        val lastStepTitle = if (submissionState is UiState.Success) null else "Minerals"

        when (currentStep) {
            1 -> "Food Information" to "Add Nutrients"
            2 -> "Nutrients" to "Add Vitamins"
            3 -> "Vitamins" to "Add Minerals"
            4 -> lastStepTitle to "Create Food"
            else -> "" to ""
        }
    }

    val titlePrefix: String? = when (foodSource) {
        FoodSource.USER -> null
        FoodSource.APP -> "Request"
    }

    val screenTitle = run {
        if (titlePrefix != null && stepTitle != null) {
            "My $titlePrefix - $stepTitle"
        } else stepTitle
    }

    Screen(
        header = {
            Header(
                onBackClick = {
                    fun onBack() {
                        onBackClick()
                        foodCreation.resetFormState()
                        onResetSubmissionState()

                        // Reset food nutrient dv map
                        val nutrientDvControls = foodCreation.nutrientDvControls
                        val nutrientDvMap = nutrientDvControls.nutrientDvMap.value
                        if (nutrientDvMap.isNotEmpty()) nutrientDvControls.onClearData()
                    }

                    fun onBackSubmissionIdle() {
                        if (currentStep == 1) {
                            onBack()
                        } else {
                            foodCreation.updateStep(currentStep, true)
                        }
                    }

                    when (submissionState) {
                        is UiState.Success -> onBack()
                        is UiState.Error -> onBackSubmissionIdle()
                        is UiState.Idle -> onBackSubmissionIdle()
                        else -> {}
                    }
                },
                isOnBackEnabled = submissionState !is UiState.Loading,
                title = screenTitle
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (nutrientsUiState is UiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(Ui.Measurements.LOADING_CIRCLE_IN_SCREEN_SIZE)
                        .align(Alignment.CenterHorizontally),
                    strokeWidth = Ui.Measurements.LOADING_CIRCLE_IN_SCREEN_STROKE_WIDTH
                )
            }

            Messages.SuccessMessageAnimated(
                isVisible = submissionState is UiState.Success,
                message = "Food submitted successfully!"
            )

            AnimatedVisibility(
                visible = nutrientsUiState is UiState.Success && submissionState !is UiState.Success,
                enter = Animation.ComposableTransition.fadeIn,
                exit = Animation.ComposableTransition.fadeOut
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxHeight()
                        .imePadding(),
                ) {
                    val nutrients = (nutrientsUiState as UiState.Success).data

                    val areNsValid = foodCreation.validateRequiredNutrients(
                        nutrientIds = nutrients.basic.map { it.nutrient }.getIds().toSet()
                    ) && currentStep >= 2

                    val areVsValid = foodCreation.validateOptionalNutrients(
                        nutrientIds = nutrients.vitamin.map { it.nutrient }.getIds().toSet()
                    ) && currentStep >= 3

                    val areMsValid = foodCreation.validateOptionalNutrients(
                        nutrientIds = nutrients.mineral.map { it.nutrient }.getIds().toSet()
                    ) && currentStep >= 4

                    if (submissionState !is UiState.Success) {
                        FormProgress(
                            currentStep = currentStep,
                            stepValidations = listOf(
                                foodCreation.isBasicDataValid,
                                areNsValid,
                                areVsValid,
                                areMsValid
                            )
                        )
                    }

                    val foodBaseFields = listOf(
                        fieldsProvider.name(
                            focusRequester = focusRequesterName
                        ),
                        fieldsProvider.brand(),
                        fieldsProvider.amountPerServing(),
                        fieldsProvider.servingUnit(
                            errorMessage = foodCreation.servingUnitError
                        ),
                    )

                    // @TODO: Use `m_26`'s `NutrientType`
                    val nutrientFieldsData = NutrientType.entries.associateWith { type ->
                        val nutrientList = nutrients
                            .filterNutrientsByType(type)
                            .filterNonPremiumPreferences(isUserPremium)

                        val nutrientsWithGoal = nutrientList.filterGoalSetPreferences()

                        val fields = nutrientsWithGoal.mapIndexed { index, nutrientWithPrefs ->
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

                        val nutrientsWithoutGoal = nutrientList
                            .filterGoalNotSet()
                            .map { it.nutrient }

                        Pair(fields, nutrientsWithoutGoal)
                    }

                    val isCurrentStepValid = when (currentStep) {
                        1 -> foodCreation.isBasicDataValid
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
                        Banners.ErrorBannerAnimated(
                            isVisible = submissionErrorMessage != null,
                            text = submissionErrorMessage ?: ""
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
                                val nutrientFields = nutrientFieldsData[NutrientType.BASIC]
                                    ?.first.orEmpty()
                                val nutrientsWithoutGoal = Pair(
                                    NutrientType.BASIC,
                                    nutrientFieldsData[NutrientType.BASIC]
                                        ?.second.orEmpty()
                                )

                                val vitaminFields = nutrientFieldsData[NutrientType.VITAMIN]
                                    ?.first.orEmpty()
                                val vitaminsWithoutGoal = Pair(
                                    NutrientType.VITAMIN,
                                    nutrientFieldsData[NutrientType.VITAMIN]
                                        ?.second.orEmpty()
                                )

                                val mineralFields = nutrientFieldsData[NutrientType.MINERAL]
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
                                        nutrientDvControls = foodCreation.nutrientDvControls
                                    )

                                    3 -> SetNutrients(
                                        fields = vitaminFields,
                                        nutrientsWithoutGoal = vitaminsWithoutGoal,
                                        nutrientDvControls = foodCreation.nutrientDvControls
                                    )

                                    4 -> SetNutrients(
                                        fields = mineralFields,
                                        nutrientsWithoutGoal = mineralsWithoutGoal,
                                        nutrientDvControls = foodCreation.nutrientDvControls
                                    )
                                }
                            }
                        }
                    }

                    NextButton(
                        enabled = isCurrentStepValid,
                        isSubmitting = submissionState is UiState.Loading,
                        text = nextButtonText
                    ) {
                        foodCreation.updateStep(
                            step = currentStep,
                            goesBack = false,
                            onSubmit = {
                                focusManager.clearFocus()
                                onSubmit()
                            }
                        )
                    }
                }
            }

            Messages.NotFoundMessageAnimated(
                isVisible = nutrientsUiState is UiState.Error,
                message = nutrientsUiState.toErrorMessageOrNull() ?: ""
            )
        }
    }
}