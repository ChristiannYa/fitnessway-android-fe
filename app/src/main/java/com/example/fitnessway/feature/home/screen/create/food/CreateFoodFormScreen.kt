package com.example.fitnessway.feature.home.screen.create.food

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.feature.home.screen.create.food.composables.FormProgressIndicator
import com.example.fitnessway.feature.home.screen.create.food.composables.NextButton
import com.example.fitnessway.feature.home.screen.create.food.composables.SetBasicData
import com.example.fitnessway.feature.home.screen.create.food.composables.SetNutrients
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Nutrient.filterNutrientsByType
import com.example.fitnessway.util.Nutrient.sortByPremiumStatus
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodCreationFieldsProvider
import org.koin.androidx.compose.koinViewModel


@Composable
fun CreateFoodFormScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()
    val foodCreationFormState by viewModel.foodCreationFormState.collectAsState()

    val fieldsProvider = FoodCreationFieldsProvider(
        formState = foodCreationFormState,
        onFieldUpdate = { fieldName, value ->
            viewModel.updateFoodCreationFormField(fieldName, value)
        }
    )

    val (title, buttonText) = when (currentStep) {
        1 -> "Food Information" to "Add Nutrients"
        2 -> "Nutrients" to "Add Vitamins"
        3 -> "Vitamins" to "Add Minerals"
        4 -> "Minerals" to "Create Food"
        else -> "" to ""
    }

    LaunchedEffect(Unit) {
        viewModel.getNutrients()
    }

    if (viewModel.user != null) {
        Screen(
            header = {
                Header(
                    onBackClick = {
                        viewModel.updateStep(
                            step = currentStep,
                            goesBack = true,
                            onExitForm = onBackClick
                        )
                    },
                    title = title
                )
            },
            content = {
                when (uiState.nutrientsState) {
                    is UiState.Loading -> Text("Loading form")

                    is UiState.Success -> Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxHeight(),
                        content = {
                            val isPremiumUser = viewModel.user.isPremium

                            val nutrients =
                                (uiState.nutrientsState as UiState.Success<NutrientsByType<NutrientApiFormat>>).data

                            val basicNutrients = filterNutrientsByType(
                                nutrients = nutrients,
                                type = NutrientType.BASIC
                            )

                            val vitamins = filterNutrientsByType(
                                nutrients = nutrients,
                                type = NutrientType.VITAMIN
                            )

                            val minerals = filterNutrientsByType(
                                nutrients = nutrients,
                                type = NutrientType.MINERAL
                            )

                            val foodBaseFields = listOf(
                                fieldsProvider.name(),
                                fieldsProvider.brand(),
                                fieldsProvider.amountPerServing(),
                                fieldsProvider.servingUnit(),
                            )

                            val foodBasicNutrientsFields = basicNutrients
                                .sortByPremiumStatus(isPremiumUser)
                                .map { (nutrient, _) ->
                                    fieldsProvider.nutrient(nutrient)
                                }

                            val foodVitaminFields = vitamins
                                .sortByPremiumStatus(isPremiumUser)
                                .map { (nutrient, _) ->
                                    fieldsProvider.nutrient(nutrient = nutrient)
                                }

                            val foodMineralFields = minerals
                                .sortByPremiumStatus(isPremiumUser)
                                .map { (nutrient, _) ->
                                    fieldsProvider.nutrient(nutrient = nutrient)
                                }

                            val areBasicNutrientsValid = viewModel.areNutrientsValid(
                                nutrients = basicNutrients.map { it.nutrient.id }.toSet()
                            )

                            val isCurrentStepValid = when (currentStep) {
                                1 -> viewModel.isBasicDataValid
                                2 -> areBasicNutrientsValid
                                3 -> true
                                4 -> true
                                else -> false
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(26.dp),
                                content = {
                                    FormProgressIndicator(
                                        currentStep = currentStep,
                                        isStepOneValid = viewModel.isBasicDataValid,
                                        isStepTwoValid = viewModel.areBasicNutrientsValid
                                    )

                                    AnimatedContent(
                                        targetState = currentStep,
                                        transitionSpec = {
                                            val isForward = targetState > initialState

                                            if (isForward)
                                                slideInHorizontally { it } + fadeIn() + scaleIn(initialScale = 0.7f) togetherWith
                                                        slideOutHorizontally { -it } + fadeOut() + scaleOut(targetScale = 0.7f)
                                            else
                                                slideInHorizontally { -it } + fadeIn() + scaleIn(initialScale = 0.7f) togetherWith
                                                        slideOutHorizontally { it } + fadeOut() + scaleOut(targetScale = 0.7f)
                                        },
                                        content = { step ->
                                            when (step) {
                                                1 -> SetBasicData(foodBaseFields)

                                                2 -> SetNutrients(
                                                    fields = foodBasicNutrientsFields,
                                                    isPremiumUser = isPremiumUser
                                                )

                                                3 -> SetNutrients(
                                                    fields = foodVitaminFields,
                                                    isPremiumUser = isPremiumUser
                                                )

                                                4 -> SetNutrients(
                                                    fields = foodMineralFields,
                                                    isPremiumUser = isPremiumUser
                                                )
                                            }
                                        }
                                    )
                                }
                            )

                            NextButton(
                                onClick = {
                                    viewModel.updateStep(
                                        step = currentStep,
                                        goesBack = false,
                                        onSubmit = { viewModel.addFood() }
                                    )
                                },
                                enabled = isCurrentStepValid,
                                text = buttonText
                            )
                        }
                    )

                    is UiState.Error -> ApiErrorMessage(
                        errMsg = (uiState.nutrientsState as UiState.Error).message
                    )

                    is UiState.Idle -> {}
                }
            }
        )
    }
}