package com.example.fitnessway.ui.edible

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.mappers.toTitleCase
import com.example.fitnessway.data.model.m_26.AppEdibleReport
import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.data.model.m_26.FoodInformationWithId
import com.example.fitnessway.feature.lists.screen.user_details.edition.composables.FoodEditionFormField
import com.example.fitnessway.ui.shared.AppVectors
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.theme.AppModifiers
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.keyboardTapDismissal
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.edible.edition.isBaseEditionValid
import com.example.fitnessway.util.edible.edition.isNutrientsEditionValid
import com.example.fitnessway.util.extensions.toPrecisedString
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.FormField
import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.util.form.field.provider.FoodEditionFieldsProvider
import com.example.fitnessway.util.nutrient.NutrientDvControls

@Composable
fun AppEdibleReportOptionsPopup(
    edible: FoodInformationWithId,
    isVisible: Boolean,
    onReport: (AppEdibleReport.Reason) -> Unit,
    modifier: Modifier = Modifier
) {
    val originalForm = FormState(
        FormStates.FoodEdition(
            name = edible.information.base.name,
            brand = edible.information.base.brand ?: "",
            amountPerServing = edible.information.base.amountPerServing.toString(),
            servingUnit = edible.information.base.servingUnit.toString().lowercase(),
            nutrients = edible.information.nutrients
                .toList()
                .associate { it.data.base.id to it.amount.toPrecisedString(4) }
        )
    )

    var form by remember { mutableStateOf(originalForm) }

    val dvControls = remember { NutrientDvControls() }
    val dvMap by dvControls.controls.nutrientDvMap.collectAsState()

    val focusManager = LocalFocusManager.current
    val fieldsProvider = FoodEditionFieldsProvider(
        formState = form,
        onFieldUpdate = { fieldName, input ->
            form = FormState(
                when (fieldName) {
                    is FormFieldName.FoodEdition.DetailField -> {
                        when (fieldName) {
                            FormFieldName.FoodEdition.DetailField.NAME -> form.data.copy(name = input)
                            FormFieldName.FoodEdition.DetailField.BRAND -> form.data.copy(brand = input)
                            FormFieldName.FoodEdition.DetailField.AMOUNT_PER_SERVING -> form.data.copy(amountPerServing = input)
                            FormFieldName.FoodEdition.DetailField.SERVING_UNIT -> form.data.copy(servingUnit = input)
                        }
                    }

                    is FormFieldName.FoodEdition.NutrientField -> {
                        val newNutrients = form.data.nutrients.toMutableMap()
                        newNutrients[fieldName.nutrient.id] = input
                        form.data.copy(nutrients = newNutrients)
                    }
                }
            )
        },
        focusManager = focusManager,
        isFormSubmitting = false
    )
    val baseFields = fieldsProvider.getBaseFields()

    var tappedReason by remember { mutableStateOf<AppEdibleReport.Reason?>(null) }
    var selectedReasons by remember { mutableStateOf<List<AppEdibleReport.Reason>>(emptyList()) }

    fun AppEdibleReport.Reason.handleAutoSelection() {
        if (!this.hasFields) return

        val isValid = when (this) {
            AppEdibleReport.Reason.INCORRECT_INFO -> isBaseEditionValid(
                form.data,
                originalForm.data,
                EdibleSource.APP
            )

            AppEdibleReport.Reason.INCORRECT_NUTRIENTS -> isNutrientsEditionValid(
                form.data.nutrients,
                originalForm.data.nutrients,
                dvMap
            )

            else -> false
        }

        val isInList = this in selectedReasons
        if (isValid && !isInList) selectedReasons += this
        else if (!isValid && isInList) selectedReasons -= this
    }

    LaunchedEffect(form.data.getBaseValues()) {
        AppEdibleReport.Reason.INCORRECT_INFO.handleAutoSelection()
    }

    LaunchedEffect(form.data.nutrients, dvMap) {
        AppEdibleReport.Reason.INCORRECT_NUTRIENTS.handleAutoSelection()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = Animation.ComposableTransition.VerticalSlideFromTop.enter,
        exit = Animation.ComposableTransition.VerticalSlideFromTop.exit,
        modifier = modifier
            .imePadding()
            .heightIn(0.dp, Ui.Measurements.POP_UP_HEIGHT_SMALL)
    ) {
        Box(
            Modifier
                .keyboardTapDismissal()
                .areaContainer(
                    size = AppModifiers.AreaContainerSize.EXTRA_LARGE,
                    borderColor = MaterialTheme.colorScheme.surfaceVariant
                )
                .animateContentSize(animationSpec = tween(durationMillis = 500))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(
                    AppEdibleReport.Reason.INCORRECT_INFO,
                    AppEdibleReport.Reason.INCORRECT_NUTRIENTS,
                    AppEdibleReport.Reason.INCORRECT_TYPE
                ).forEach { reason ->
                    val isTapped = reason == tappedReason

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        val isInList = reason in selectedReasons

                        Box(
                            Modifier
                                .areaContainer(
                                    size = AppModifiers.AreaContainerSize.SMALL,
                                    areaColor = if (isTapped && reason.hasFields) {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    } else MaterialTheme.colorScheme.secondaryContainer,
                                    isTapIndicationVisible = !reason.hasFields
                                ) {
                                    if (tappedReason == reason) {
                                        tappedReason = null
                                        if (!reason.hasFields) selectedReasons -= reason
                                    } else {
                                        tappedReason = reason
                                        if (!reason.hasFields && !isInList) selectedReasons += reason
                                    }
                                }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    val textColor = if (isTapped && reason.hasFields) {
                                        MaterialTheme.colorScheme.inverseOnSurface
                                    } else MaterialTheme.colorScheme.onSurfaceVariant

                                    Text(
                                        text = buildAnnotatedString {
                                            append(reason.toString().toTitleCase(false))

                                            if (reason == AppEdibleReport.Reason.INCORRECT_TYPE) {
                                                withStyle(
                                                    style = SpanStyle(
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                                                    )
                                                ) {
                                                    append(
                                                        " (Current: ${
                                                            edible.information.type.toString().toTitleCase()
                                                        })"
                                                    )
                                                }
                                            }
                                        },
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = robotoSerifFamily,
                                        color = textColor,
                                    )

                                    if (isTapped && reason.hasFields) {
                                        Text(
                                            text = "Please provide the correct fields",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = textColor
                                        )
                                    }
                                }

                                Structure.AppIconDynamic(
                                    source = Structure.AppIconSource.Vector(AppVectors.checkmark),
                                    tint = if (isInList) {
                                        if (reason.hasFields && isTapped) MaterialTheme.colorScheme.secondaryContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    } else Color.Transparent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        val optionFieldScrollStates = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .verticalScroll(optionFieldScrollStates)
                                .padding(top = if (isTapped && reason.hasFields) 8.dp else 0.dp)
                        ) {
                            when (tappedReason) {
                                AppEdibleReport.Reason.INCORRECT_INFO ->
                                    if (reason == AppEdibleReport.Reason.INCORRECT_INFO) {
                                        Fields(
                                            fields = baseFields,
                                            onGetOriginalField = {
                                                when (it.name) {
                                                    FormFieldName.FoodEdition.DetailField.NAME -> originalForm.data.name
                                                    FormFieldName.FoodEdition.DetailField.BRAND -> originalForm.data.brand
                                                    FormFieldName.FoodEdition.DetailField.AMOUNT_PER_SERVING -> originalForm.data.amountPerServing
                                                    FormFieldName.FoodEdition.DetailField.SERVING_UNIT -> originalForm.data.servingUnit
                                                }
                                            }
                                        ) { FoodEditionFormField(it) }
                                    }

                                AppEdibleReport.Reason.INCORRECT_NUTRIENTS ->
                                    if (reason == AppEdibleReport.Reason.INCORRECT_NUTRIENTS) {
                                        val nutrientList = edible.information.nutrients.toList()

                                        val nutrientFields = nutrientList.map {
                                            fieldsProvider.nutrient(
                                                nutrient = it.data.base,
                                                isLastField = it.data.base.id == nutrientList.last().data.base.id
                                            )
                                        }

                                        Fields(
                                            fields = nutrientFields,
                                            onGetOriginalField = {
                                                val nutrient = it.name.nutrient
                                                val unit = nutrient.unit.toString().lowercase()
                                                val id = nutrient.id
                                                val original = originalForm.data.nutrients.getValue(id)
                                                dvMap[id]
                                                    ?.let { "$original $unit" }
                                                    ?: original
                                            }
                                        ) {
                                            FoodEditionFormField(
                                                field = it,
                                                nutrientDvControls = dvControls.controls
                                            )
                                        }
                                    }

                                else -> {}
                            }
                        }
                    }
                }

                TextButton(
                    onClick = { tappedReason?.let { onReport(it) } },
                    enabled = selectedReasons.isNotEmpty(),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = WhiteFont
                    )
                ) {
                    Text(
                        text = "Report",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun <T : FormFieldName.IFoodEdition> Fields(
    fields: List<FormField<T>>,
    onGetOriginalField: (FormField<T>) -> String,
    content: @Composable (FormField<T>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        fields.forEach {
            Column {
                content(it)

                val originalValue = onGetOriginalField(it)

                if (originalValue != (it.textFieldValue?.text ?: "")) {
                    Text(
                        text = "Was: $originalValue",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
            }
        }
    }
}
