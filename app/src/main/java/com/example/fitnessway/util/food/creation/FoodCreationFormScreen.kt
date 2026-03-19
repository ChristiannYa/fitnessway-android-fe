package com.example.fitnessway.util.food.creation

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.ui.shared.Banners
import com.example.fitnessway.ui.shared.FormProgress
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Messages
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.IFoodCreation
import com.example.fitnessway.util.UNutrient.filterGoalNotSet
import com.example.fitnessway.util.UNutrient.filterGoalSetPreferences
import com.example.fitnessway.util.UNutrient.filterNonPremiumPreferences
import com.example.fitnessway.util.UNutrient.filterNutrientsByType
import com.example.fitnessway.util.UNutrient.getIds
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodCreationFieldsProvider
import kotlinx.coroutines.delay

@Composable
fun <T> FoodCreationFormScreen(
    onBackClick: () -> Unit,
    foodCreation: IFoodCreation,
    nutrients: MNutrient.Model.NutrientsByType<MNutrient.Model.NutrientWithPreferences>,
    isUserPremium: Boolean,
    onResetSubmissionState: () -> Unit,
    submissionState: UiState<T>,
    submissionErrorMessage: String?,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterNutrient = remember { FocusRequester() }
    val focusRequesterVitamin = remember { FocusRequester() }
    val focusRequesterMineral = remember { FocusRequester() }

    val fieldsProvider = FoodCreationFieldsProvider(
        formState = foodCreation.formState.collectAsState().value,
        onFieldUpdate = foodCreation::updateFormField,
        focusManager = focusManager,
        isFormSubmitting = submissionState is UiState.Loading
    )

    LaunchedEffect(foodCreation.currentStep.collectAsState().value) {
        val focusRequester = when (foodCreation.currentStep.value) {
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

    val areNsValid = foodCreation.validateRequiredNutrients(
        nutrientIds = nutrients.basic.map { it.nutrient }.getIds().toSet()
    ) && foodCreation.currentStep.collectAsState().value >= 2

    val areVsValid = foodCreation.validateOptionalNutrients(
        nutrientIds = nutrients.vitamin.map { it.nutrient }.getIds().toSet()
    ) && foodCreation.currentStep.collectAsState().value >= 3

    val areMsValid = foodCreation.validateOptionalNutrients(
        nutrientIds = nutrients.mineral.map { it.nutrient }.getIds().toSet()
    ) && foodCreation.currentStep.collectAsState().value >= 4

    val (title, nextButtonText) = getFormTitle(
        isSubmissionSuccessful = submissionState is UiState.Success,
        currentStep = foodCreation.currentStep.collectAsState().value
    )

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

                    fun onBackAddIdle() {
                        if (foodCreation.currentStep.value == 1) {
                            onBack()
                        } else {
                            foodCreation.updateStep(foodCreation.currentStep.value, true)
                        }
                    }

                    when (submissionState) {
                        is UiState.Success -> onBack()
                        is UiState.Error -> onBackAddIdle()
                        is UiState.Idle -> onBackAddIdle()
                        else -> {}
                    }
                },
                isOnBackEnabled = submissionState !is UiState.Loading,
                title = title,
                extraContent = {
                    if (submissionState !is UiState.Success) {
                        FormProgress(
                            currentStep = foodCreation.currentStep.collectAsState().value,
                            stepValidations = listOf(
                                foodCreation.isBasicDataValid,
                                areNsValid,
                                areVsValid,
                                areMsValid
                            )
                        )
                    }
                }
            )
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Messages.SuccessMessageAnimated(
                isVisible = submissionState is UiState.Success,
                message = "Food submitted successfully!"
            )

            AnimatedVisibility(
                visible = submissionState !is UiState.Success,
                enter = Animation.ComposableTransition.fadeIn,
                exit = Animation.ComposableTransition.fadeOut
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxHeight()
                        .imePadding(),
                ) {
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

                    val isCurrentStepValid = when (foodCreation.currentStep.collectAsState().value) {
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
                                targetState = foodCreation.currentStep.collectAsState().value,
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
                            step = foodCreation.currentStep.value,
                            goesBack = false,
                            onSubmit = {
                                focusManager.clearFocus()
                                onSubmit()
                            }
                        )
                    }
                }
            }
        }
    }
}