package com.example.fitnessway.feature.lists.screen.user_details.edition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.mappers.toM25NutrientDataWithAmount
import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.feature.lists.screen.user_details.edition.composables.FoodEditionFormField
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Banners.ErrorBannerAnimated
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Messages
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.ScreenOverlay
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.UNutrient
import com.example.fitnessway.util.UNutrient.combine
import com.example.fitnessway.util.UNutrient.filterNonPremiumPreferences
import com.example.fitnessway.util.UNutrient.toReadable
import com.example.fitnessway.util.UNutrient.toTypedList
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.Ui.handleTempApiErrMsg
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.provider.FoodEditionFieldsProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodEditionScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val foodEditionFormState by viewModel.editionManager.edibleEditionFormState.collectAsState()
    val selectedFood by viewModel.editionManager.selectedEdible.collectAsState()
    val addedNutrients by viewModel.editionManager.addedNutrients.collectAsState()
    val deletedNutrients by viewModel.editionManager.deletedNutrients.collectAsState()
    val isFoodEditionFormValid by viewModel.editionManager.isEdibleEditionFormValid.collectAsState()

    val user = userFlow
    val nutrientUiState = nutrientRepoUiState.nutrientsUiState
    val foodUpdateState = uiState.foodUpdateState
    val nutrientDvControls = viewModel.editionManager

    val foodUpdateErrorMessage = handleTempApiErrMsg(
        uiState = foodUpdateState,
        onTimeOut = viewModel::resetFoodUpdateState
    )

    val selectedFoodCopy = selectedFood
    val foodEditionFormStateCopy = foodEditionFormState
    val areDelegatesPresent = selectedFoodCopy != null && foodEditionFormStateCopy != null

    val title = "Food Edition"
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val scrollState = rememberScrollState()

    var availableNutrientsType by remember { mutableStateOf(NutrientType.BASIC) }
    var isAvailableNutrientsVisible by remember { mutableStateOf(false) }

    if (user != null && areDelegatesPresent && nutrientUiState is UiState.Success) {
        Screen(
            header = {
                Header(
                    onBackClick = {
                        viewModel.resetFoodEditionStates()
                        onBackClick()
                    },
                    title = title
                ) {
                    Clickables.HeaderDoneButton(
                        enabled = isFoodEditionFormValid
                    ) {
                        focusManager.clearFocus()
                        if (foodUpdateState !is UiState.Idle) viewModel.resetFoodUpdateState()
                        viewModel.updateEdible()
                    }
                }
            }
        ) {
            val fieldsProvider = FoodEditionFieldsProvider(
                formState = foodEditionFormStateCopy,
                focusManager = focusManager,
                isFormSubmitting = foodUpdateState is UiState.Loading,
                onFieldUpdate = { fieldName, value ->
                    viewModel.editionManager.updateEdibleEditionFormField(fieldName, value)
                }
            )

            val detailFields = listOf(
                fieldsProvider.name(),
                fieldsProvider.brand(),
                fieldsProvider.amountPerServing(),
                fieldsProvider.servingUnit()
            )
            var nutrientIdsPresent: List<Int>

            val foodWithAddedNutrients = (selectedFoodCopy.information.nutrients
                .toList()
                .map { n -> n.toM25NutrientDataWithAmount().nutrientWithPreferences.nutrient } + addedNutrients)
                .distinctBy { it.id }
                .filter { it.id !in deletedNutrients }
                .also { nutrientIdsPresent = it.map { n -> n.id } }

            val editableNutrients = UNutrient.buildNutrientsByType2(
                nutrients = foodWithAddedNutrients,
                getType = { it.type }
            ).toTypedList()

            val nutrientFields = editableNutrients.map { (type, nutrients) ->
                type to nutrients.map { fieldsProvider.nutrient(it, it.id == nutrientIdsPresent.last()) }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.imePadding()
                ) {
                    ErrorBannerAnimated(
                        isVisible = foodUpdateErrorMessage != null,
                        text = foodUpdateErrorMessage ?: ""
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.verticalScroll(scrollState)
                    ) {
                        FieldSection(
                            title = "Details",
                            fields = detailFields
                        ) { FoodEditionFormField(it) }

                        nutrientFields.forEach { (type, fields) ->
                            FieldSection(
                                title = type.toReadable(),
                                fields = fields,
                                onViewAvailableNutrients = {
                                    if (isImeVisible) focusManager.clearFocus()
                                    availableNutrientsType = type
                                    isAvailableNutrientsVisible = true
                                }
                            ) {
                                FoodEditionFormField(
                                    field = it,
                                    onRemoveNutrient = viewModel.editionManager::filterOutNutrientFromForm,
                                    nutrientDvControls = viewModel.editionManager.nutrientDvControls
                                )
                            }
                        }
                    }
                }

                ScreenOverlay.DarkOverlay(
                    isVisible = isAvailableNutrientsVisible,
                    onClick = { isAvailableNutrientsVisible = false }
                )

                val availableNutrientsClickConfig = Ui.ClickableConfiguration<MNutrient.Model.Nutrient>(
                    onClick = viewModel.editionManager::addNutrientToForm
                )

                val nutrients = nutrientUiState.data
                    .combine()
                    .filterNonPremiumPreferences(user.isPremium)
                    .map { it.nutrient }

                val availableNutrients = nutrients.filter {
                    it.id !in foodEditionFormStateCopy.data.nutrients.keys.toList()
                }

                val availableNutrientsByType = availableNutrients.filter { it.type == availableNutrientsType }

                AvailableNutrientsPopup(
                    isVisible = isAvailableNutrientsVisible,
                    availableNutrients = availableNutrientsByType,
                    nutrientType = availableNutrientsType,
                    clickableConfiguration = availableNutrientsClickConfig,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        }
    } else NotFoundScreen(
        onBackClick = onBackClick,
        title = title,
        message = "Data not found"
    )
}

@Composable
private fun <T> FieldSection(
    title: String,
    fields: List<T>,
    onViewAvailableNutrients: (() -> Unit)? = null,
    content: @Composable (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
            )

            if (onViewAvailableNutrients != null) {
                Clickables.AppPngIconButton(
                    icon = Structure.AppIconSource.Vector(Icons.Default.Add),
                    contentDescription = "Add nutrient",
                    onClick = onViewAvailableNutrients
                )
            }
        }

        Box {
            if (fields.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    fields.forEach { content(it) }
                }
            } else {
                Box(
                    modifier = Modifier
                        .areaContainer(
                            areaColor = Ui.InputUi.getOutlinedColors().disabledContainerColor,
                            onClick = onViewAvailableNutrients,
                            showsIndication = true
                        )
                ) {
                    Messages.NotFoundMessage(
                        message = "No $title to add",
                        fillsWidth = false
                    )
                }
            }
        }
    }
}

@Composable
private fun AvailableNutrientsPopup(
    isVisible: Boolean,
    availableNutrients: List<MNutrient.Model.Nutrient>,
    nutrientType: NutrientType,
    clickableConfiguration: Ui.ClickableConfiguration<MNutrient.Model.Nutrient>,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = Animation.ComposableTransition.VerticalSlideExtra.enter,
        exit = Animation.ComposableTransition.VerticalSlideExtra.exit,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .areaContainer(
                    areaColor = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(32.dp),
                    hugsContent = true
                )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UNutrient.Ui.NutrientCategoryTitle(
                    type = nutrientType,
                    style = MaterialTheme.typography.titleLarge
                )

                val labelColor = MaterialTheme.colorScheme.surfaceTint

                if (availableNutrients.isNotEmpty()) {
                    UNutrient.Ui.NutrientLabelsFlowRow(
                        nutrients = availableNutrients,
                        size = Ui.LabelSize.Xl,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        getColor = { labelColor },
                        clickableConfiguration = clickableConfiguration
                    )
                } else {
                    Messages.NotFoundMessage(
                        message = "No ${nutrientType.toReadable(isLowercase = true)} to add",
                        fillsWidth = false
                    )
                }
            }
        }
    }
}