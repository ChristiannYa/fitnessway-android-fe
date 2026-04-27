package com.example.fitnessway.util.edible.creation.composables

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
import com.example.fitnessway.data.mappers.toPascalSpaced
import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.ui.shared.Banners
import com.example.fitnessway.ui.shared.FormProgress
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Messages
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.UNutrient.filterGoalNotSet
import com.example.fitnessway.util.UNutrient.filterNonPremiumPreferences
import com.example.fitnessway.util.UNutrient.filterNutrientsByType
import com.example.fitnessway.util.UNutrient.getIds
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.edible.creation.IEdibleCreation
import com.example.fitnessway.util.form.field.provider.FoodCreationFieldsProvider
import kotlinx.coroutines.delay

@Composable
fun <T> FoodCreationFormScreen(
    onBackClick: () -> Unit,
    edibleCreation: IEdibleCreation,
    edibleSource: EdibleSource,
    edibleType: EdibleType,
    nutrientsUiState: UiState<MNutrient.Model.NutrientsByType<MNutrient.Model.NutrientWithPreferences>>,
    isUserPremium: Boolean,
    onResetSubmissionState: () -> Unit,
    submissionState: UiState<T>,
    submissionErrorMessage: String?,
    onSubmit: () -> Unit,
) {
    val formState by edibleCreation.formState.collectAsState()
    val currentStep by edibleCreation.currentStep.collectAsState()

    val focusManager = LocalFocusManager.current
    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterNutrient = remember { FocusRequester() }
    val focusRequesterVitamin = remember { FocusRequester() }
    val focusRequesterMineral = remember { FocusRequester() }

    val fieldsProvider = FoodCreationFieldsProvider(
        formState = formState,
        onFieldUpdate = edibleCreation::updateFormField,
        focusManager = focusManager,
        isFormSubmitting = submissionState is UiState.Loading
    )

    val edibleTypePascalSpaced = edibleType.name.toPascalSpaced()

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
            1 -> "$edibleTypePascalSpaced Information" to "Add Nutrients"
            2 -> "Nutrients" to "Add Vitamins"
            3 -> "Vitamins" to "Add Minerals"
            4 -> lastStepTitle to "Create $edibleTypePascalSpaced"
            else -> "" to ""
        }
    }

    val titlePrefix: String? = when (edibleSource) {
        EdibleSource.USER -> null
        EdibleSource.APP -> "Request"
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
                        edibleCreation.resetFormState()
                        onResetSubmissionState()

                        // Reset food nutrient dv map
                        val nutrientDvControls = edibleCreation.nutrientDvControls
                        val nutrientDvMap = nutrientDvControls.nutrientDvMap.value
                        if (nutrientDvMap.isNotEmpty()) nutrientDvControls.onClearData()
                    }

                    fun onBackSubmissionIdle() {
                        if (currentStep == 1) {
                            onBack()
                        } else {
                            edibleCreation.updateStep(currentStep, true)
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
                message = "$edibleTypePascalSpaced submitted successfully!"
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

                    val areNsValid = edibleCreation.validateRequiredNutrients(
                        nutrientIds = nutrients.basic.map { it.nutrient }.getIds().toSet()
                    ) && currentStep >= 2

                    val areVsValid = edibleCreation.validateOptionalNutrients(
                        nutrientIds = nutrients.vitamin.map { it.nutrient }.getIds().toSet()
                    ) && currentStep >= 3

                    val areMsValid = edibleCreation.validateOptionalNutrients(
                        nutrientIds = nutrients.mineral.map { it.nutrient }.getIds().toSet()
                    ) && currentStep >= 4

                    if (submissionState !is UiState.Success) {
                        FormProgress(
                            currentStep = currentStep,
                            stepValidations = listOf(
                                edibleCreation.isBasicDataValid,
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
                            errorMessage = edibleCreation.servingUnitError
                        ),
                    )

                    val nutrientFieldsData = NutrientType.entries.associateWith { type ->
                        val nutrientList = nutrients
                            .filterNutrientsByType(type)
                            .let {
                                if (edibleSource == EdibleSource.USER) {
                                    it.filterNonPremiumPreferences(isUserPremium)
                                } else it
                            }


                        val nutrientsWithGoal = nutrientList.let {
                            if (edibleSource == EdibleSource.USER) {
                                it.filter { n -> n.preferences.goal != null }
                            } else it
                        }

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

                        val nutrientsWithoutGoal = if (edibleSource == EdibleSource.USER) {
                            nutrientList
                                .filterGoalNotSet()
                                .map { it.nutrient }
                        } else null

                        Pair(fields, nutrientsWithoutGoal)
                    }

                    val isCurrentStepValid = when (currentStep) {
                        1 -> edibleCreation.isBasicDataValid
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
                                fun getFieldsAndNutrients(type: NutrientType) = Pair(
                                    nutrientFieldsData[type]?.first.orEmpty(),
                                    run {
                                        nutrientFieldsData[type]?.second?.let {
                                            Pair(type, it)
                                        }
                                    }
                                )

                                val (nutrientFields, nutrientsWithoutGoal) =
                                    getFieldsAndNutrients(NutrientType.BASIC)

                                val (vitaminFields, vitaminsWithoutGoal) =
                                    getFieldsAndNutrients(NutrientType.VITAMIN)

                                val (mineralFields, mineralsWithoutGoal) =
                                    getFieldsAndNutrients(NutrientType.MINERAL)

                                when (step) {
                                    1 -> SetBasicData(foodBaseFields)

                                    2 -> SetNutrients(
                                        fields = nutrientFields,
                                        nutrientsWithoutGoal = nutrientsWithoutGoal,
                                        nutrientDvControls = edibleCreation.nutrientDvControls
                                    )

                                    3 -> SetNutrients(
                                        fields = vitaminFields,
                                        nutrientsWithoutGoal = vitaminsWithoutGoal,
                                        nutrientDvControls = edibleCreation.nutrientDvControls
                                    )

                                    4 -> SetNutrients(
                                        fields = mineralFields,
                                        nutrientsWithoutGoal = mineralsWithoutGoal,
                                        nutrientDvControls = edibleCreation.nutrientDvControls
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
                        edibleCreation.updateStep(
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