package com.example.fitnessway.ui.edible

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
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
import com.example.fitnessway.data.mappers.toSuccessOrNull
import com.example.fitnessway.data.mappers.toTitleCase
import com.example.fitnessway.data.model.m_26.AppEdibleReport
import com.example.fitnessway.data.model.m_26.AppEdibleReportRequest
import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.data.model.m_26.FoodInformationWithId
import com.example.fitnessway.data.model.m_26.NutrientData
import com.example.fitnessway.data.model.m_26.NutrientsByType
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
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.edible.edition.isBaseEditionValid
import com.example.fitnessway.util.edible.edition.isNutrientsEditionValid
import com.example.fitnessway.util.extensions.toTruncatedDecimalString
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.FormField
import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.util.form.field.provider.FoodEditionFieldsProvider
import com.example.fitnessway.util.nutrient.NutrientDvControls
import com.example.fitnessway.util.nutrient.getNutrientDv

@Composable
fun AppEdibleReportOptionsPopup(
    edible: FoodInformationWithId,
    nutrientsUiState: UiState<NutrientsByType<NutrientData>>,
    isVisible: Boolean,
    isReportConfirmed: Boolean,
    onReport: (AppEdibleReportRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    val originalForm by remember(edible.id) {
        mutableStateOf(
            FormState(
                FormStates.FoodEdition(
                    name = edible.information.base.name,
                    brand = edible.information.base.brand ?: "",
                    amountPerServing = edible.information.base.amountPerServing.toTruncatedDecimalString(4),
                    servingUnit = edible.information.base.servingUnit.toString().lowercase(),
                    nutrients = edible.information.nutrients
                        .toList()
                        .associate { it.data.base.id to it.amount.toTruncatedDecimalString(4) }
                )
            )
        )
    }

    var form by remember(edible.id) { mutableStateOf(originalForm) }

    val dvControls = remember(edible.id) { NutrientDvControls() }
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

    val edibleNutrientList = edible.information.nutrients.toList()
    val edibleNutrientIdsSet = edibleNutrientList.map { it.data.base.id }.toSet()
    var removedNutrientIdsSet by remember(edible.id) { mutableStateOf(emptySet<Int>()) }
    val remainingNutrients = nutrientsUiState
        .toSuccessOrNull()
        ?.toList()
        ?.map { it.base }
        ?.filter { it.id !in edibleNutrientIdsSet || it.id in removedNutrientIdsSet }
        ?: emptyList()
    val remainingNutrientIdsSet = remainingNutrients.map { it.id }.toSet()

    val baseFields = fieldsProvider.getBaseFields()
    val currentNutrientFields = edibleNutrientList
        .filter { it.data.base.id !in removedNutrientIdsSet }
        .map {
            fieldsProvider.nutrient(
                nutrient = it.data.base,
                isLastField = it.data.base.id == edibleNutrientIdsSet.last()
            )
        }
    val remainingNutrientFields = remainingNutrients.map {
        fieldsProvider.nutrient(it, it.id == remainingNutrientIdsSet.last())
    }

    var isSuggestingMissingNutrients by remember { mutableStateOf(false) }

    var tappedReason by remember { mutableStateOf<AppEdibleReport.Reason?>(null) }
    var selectedReasons by remember { mutableStateOf<List<AppEdibleReport.Reason>>(emptyList()) }

    fun autoHandleReasonSelection(reason: AppEdibleReport.Reason) {
        if (!reason.hasFields) return

        val isValid = when (reason) {
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

        val isInList = reason in selectedReasons
        if (isValid && !isInList) selectedReasons += reason
        else if (!isValid && isInList) selectedReasons -= reason
    }

    fun onSuggestMissingNutrientsToggle() {
        isSuggestingMissingNutrients = !isSuggestingMissingNutrients
    }

    fun onTapReason(reason: AppEdibleReport.Reason) {
        tappedReason = if (reason == tappedReason) null else reason

        if (!reason.hasFields) {
            if (reason in selectedReasons) selectedReasons -= reason
            else selectedReasons += reason
        }

        if (isSuggestingMissingNutrients) isSuggestingMissingNutrients = false
    }

    fun onRemoveNutrient(nutrientId: Int) {
        removedNutrientIdsSet = removedNutrientIdsSet.plus(nutrientId)
        val newNutrients = form.data.nutrients.toMutableMap()
        newNutrients.remove(nutrientId)
        form = FormState(form.data.copy(nutrients = newNutrients))
    }

    fun onAddNutrientBack(nutrientId: Int) {
        removedNutrientIdsSet = removedNutrientIdsSet.minus(nutrientId)
        val newNutrients = form.data.nutrients.toMutableMap()
        newNutrients[nutrientId] = originalForm.data.nutrients.getValue(nutrientId)
        form = FormState(form.data.copy(nutrients = newNutrients))
    }

    fun onReport() {
        val informationChangeText = if (AppEdibleReport.Reason.INCORRECT_INFO in selectedReasons) {
            val changedLines = baseFields.mapNotNull { field ->
                val current = field.textFieldValue?.text ?: field.value

                val (label, original) = when (field.name) {
                    FormFieldName.FoodEdition.DetailField.NAME ->
                        "Name" to originalForm.data.name

                    FormFieldName.FoodEdition.DetailField.BRAND ->
                        "Brand" to originalForm.data.brand

                    FormFieldName.FoodEdition.DetailField.AMOUNT_PER_SERVING ->
                        "Amount per serving" to originalForm.data.amountPerServing

                    FormFieldName.FoodEdition.DetailField.SERVING_UNIT ->
                        "Serving unit" to originalForm.data.servingUnit
                }

                if (current != original) "  ~ $label: $original -> $current" else null
            }

            if (changedLines.isEmpty()) null
            else buildString {
                appendLine("Suggested information")
                changedLines.forEach { appendLine(it) }
            }.trimEnd()
        } else null

        val nutrientsChangeText = if (AppEdibleReport.Reason.INCORRECT_NUTRIENTS in selectedReasons) {
            val changedLines = currentNutrientFields.mapNotNull { field ->
                val nutrient = field.name.nutrient
                val id = nutrient.id
                val name = nutrient.name.toTitleCase(false)
                val unit = nutrient.unit.toString().lowercase()

                val current = field.textFieldValue?.text ?: field.value
                val original = originalForm.data.nutrients.getValue(id)

                val isDv = dvMap.containsKey(id)

                val originalDisplay = if (isDv) "$original$unit" else "$original$unit"
                val currentDisplay =
                    if (isDv) "$current% (${getNutrientDv(id, current.toDouble())}$unit)"
                    else "$current$unit"

                if (current != original || isDv) "  ~ $name $originalDisplay -> $currentDisplay"
                else null
            }

            val suggestedLines: List<String> = remainingNutrients.mapNotNull { remainingNutrient ->
                remainingNutrientFields
                    .find { remainingField ->
                        val fieldNotBlank = remainingField.textFieldValue?.text?.isNotBlank() == true
                        val isField = remainingField.name.nutrient.id == remainingNutrient.id
                        fieldNotBlank && isField
                    }
                    ?.let { remainingField ->
                        val amount = remainingField.textFieldValue?.text ?: remainingField.value
                        val isDv = dvMap.containsKey(remainingNutrient.id)
                        val unit = remainingNutrient.unit.toString().lowercase()
                        val display =
                            if (isDv) "${amount}% (${getNutrientDv(remainingNutrient.id, amount.toDouble())})"
                            else "$amount$unit"
                        "  + ${remainingNutrient.name}: $display"
                    }
            }

            val removedLines: List<String> = remainingNutrientFields
                .filter { it.name.nutrient.id in originalForm.data.nutrients.keys }
                .map {
                    val nutrient = it.name.nutrient
                    val amount = originalForm.data.nutrients.getValue(nutrient.id)
                    val unit = nutrient.unit.toString().lowercase()
                    "  - ${nutrient.name}: $amount$unit"
                }

            val isChangedLinesEmpty = changedLines.isEmpty()
            val isSuggestedLinesEmpty = suggestedLines.isEmpty()
            val isRemovedLinesEmpty = removedLines.isEmpty()

            if (isChangedLinesEmpty && isSuggestedLinesEmpty && isRemovedLinesEmpty) null
            else buildString {

                if (!isChangedLinesEmpty) {
                    appendLine("Changed nutrients")
                    changedLines.forEach { appendLine(it) }
                    appendLine("")
                }

                if (!isSuggestedLinesEmpty) {
                    appendLine("Suggested nutrients")
                    suggestedLines.forEach { appendLine(it) }
                    appendLine("")
                }

                if (!isRemovedLinesEmpty) {
                    appendLine("Removed nutrients")
                    removedLines.forEach { appendLine(it) }
                    appendLine("")
                }

            }.trimEnd()
        } else null

        val notes = listOfNotNull(informationChangeText, nutrientsChangeText)
            .joinToString("\n\n")
            .ifBlank { null }

        onReport(
            AppEdibleReportRequest(
                edibleId = edible.id,
                reasons = selectedReasons.map { r -> r.toString().lowercase() },
                notes = notes
            )
        )
    }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            if (tappedReason?.hasFields == true) tappedReason = null
        }
    }

    LaunchedEffect(isReportConfirmed, isVisible) {
        if (isReportConfirmed && !isVisible) {
            tappedReason = null
            selectedReasons = emptyList()
            form = originalForm
            removedNutrientIdsSet = emptySet()
            dvControls.controls.onClearData()
        }
    }

    LaunchedEffect(form.data.getBaseValues()) {
        autoHandleReasonSelection(AppEdibleReport.Reason.INCORRECT_INFO)
    }

    LaunchedEffect(form.data.nutrients, dvMap) {
        // Handle removal of added remaining nutrient to the form if its value is blank
        form.data.nutrients.also { current ->
            if (!current.keys.any { it in remainingNutrientIdsSet }) return@also
            val remainingBlanks = current.filter { (id, amount) -> id in remainingNutrientIdsSet && amount.isBlank() }

            if (remainingBlanks.keys.isEmpty()) return@also
            val newCurrent = current.minus(remainingBlanks.keys)
            form = FormState(form.data.copy(nutrients = newCurrent))
        }

        autoHandleReasonSelection(AppEdibleReport.Reason.INCORRECT_NUTRIENTS)
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
            when (nutrientsUiState) {
                is UiState.Loading -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(
                        strokeWidth = Ui.Measurements.LOADING_CIRCLE_IN_HEADER_STROKE_WIDTH,
                        modifier = Modifier.size(Ui.Measurements.LOADING_CIRCLE_IN_HEADER_SIZE)
                    )
                }

                is UiState.Success -> {
                    Column {
                        val reportableReasons = AppEdibleReport.Reason.entries
                            .filter { it != AppEdibleReport.Reason.INCORRECT_BARCODE }

                        reportableReasons.forEachIndexed { index, reason ->
                            val isTapped = reason == tappedReason
                            val isTappedReasonWithFields = isTapped && tappedReason?.hasFields == true
                            val isReasonButtonVisible = tappedReason == null ||
                                    tappedReason?.hasFields == false ||
                                    isTappedReasonWithFields

                            if (index != 0 && (tappedReason == null || tappedReason?.hasFields == false)) {
                                Spacer(Modifier.height(16.dp))
                            }

                            Column {
                                val isInList = reason in selectedReasons

                                if (isReasonButtonVisible) {
                                    Box(
                                        Modifier
                                            .areaContainer(
                                                size = AppModifiers.AreaContainerSize.SMALL,
                                                areaColor = if (isTapped && reason.hasFields) {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                } else MaterialTheme.colorScheme.secondaryContainer,
                                                isTapIndicationVisible = !reason.hasFields,
                                                onClick = { onTapReason(reason) }
                                            )
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

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = buildAnnotatedString {
                                                            append(reason.toString().toTitleCase(false))

                                                            if (reason == AppEdibleReport.Reason.INCORRECT_TYPE) {
                                                                withStyle(
                                                                    style = SpanStyle(
                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                            .copy(0.6f)
                                                                    )
                                                                ) {
                                                                    append(
                                                                        " (Current: ${
                                                                            edible.information.type.toString()
                                                                                .toTitleCase()
                                                                        })"
                                                                    )
                                                                }
                                                            }
                                                        },
                                                        fontWeight = FontWeight.Medium,
                                                        fontFamily = robotoSerifFamily,
                                                        color = textColor,
                                                    )

                                                    if (reason.hasFields) {
                                                        Structure.AppIconDynamic(
                                                            source = Structure.AppIconSource.Vector(
                                                                if (isTapped) Icons.Default.ArrowDropUp
                                                                else Icons.Default.ArrowDropDown
                                                            ),
                                                            tint = if (isTapped) MaterialTheme.colorScheme.secondaryContainer
                                                            else MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }


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
                                }

                                val optionFieldScrollStates = rememberScrollState()
                                Column(
                                    modifier = Modifier
                                        .verticalScroll(optionFieldScrollStates)
                                        .padding(top = if (isTapped && reason.hasFields) 8.dp else 0.dp)
                                        .then(
                                            if (isTapped && reason.hasFields) {
                                                Modifier.weight(1f)
                                            } else Modifier
                                        )
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
                                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                                    AnimatedVisibility(
                                                        visible = !isSuggestingMissingNutrients,
                                                        enter = Animation.ComposableTransition.fadeIn,
                                                        exit = Animation.ComposableTransition.fadeOut
                                                    ) {

                                                        Fields(
                                                            fields = currentNutrientFields,
                                                            onGetOriginalField = {
                                                                val nutrient = it.name.nutrient
                                                                val unit = nutrient.unit.toString().lowercase()
                                                                val id = nutrient.id
                                                                val original =
                                                                    originalForm.data.nutrients.getValue(id)
                                                                dvMap[id]
                                                                    ?.let { "$original $unit" }
                                                                    ?: original
                                                            }
                                                        ) { field ->
                                                            FoodEditionFormField(
                                                                field = field,
                                                                nutrientDvControls = dvControls.controls,
                                                                onRemoveNutrient = { onRemoveNutrient(it.id) }
                                                            )
                                                        }
                                                    }

                                                    AnimatedVisibility(
                                                        visible = isSuggestingMissingNutrients,
                                                        enter = Animation.ComposableTransition.fadeIn,
                                                        exit = Animation.ComposableTransition.fadeOut
                                                    ) {
                                                        Fields(
                                                            fields = remainingNutrientFields,
                                                            onGetOriginalField = { "" }
                                                        ) {
                                                            val nutrient = it.name.nutrient

                                                            if (nutrient.id !in originalForm.data.nutrients.keys) {
                                                                FoodEditionFormField(
                                                                    field = it,
                                                                    nutrientDvControls = dvControls.controls
                                                                )
                                                            } else {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .padding(top = (LocalTextStyle.current.lineHeight / 2).value.dp - 1.dp)
                                                                        .areaContainer(
                                                                            size = AppModifiers.AreaContainerSize.MEDIUM,
                                                                            areaColor = Ui.InputUi.getOutlinedColors().unfocusedContainerColor,
                                                                            shape = Ui.InputUi.shape,
                                                                            onClick = { onAddNutrientBack(nutrient.id) }
                                                                        )
                                                                ) {
                                                                    Text(
                                                                        // text = "Add ${nutrient.name} back",
                                                                        text = buildAnnotatedString {
                                                                            append("Add ")

                                                                            withStyle(
                                                                                style = SpanStyle(
                                                                                    fontWeight = FontWeight.SemiBold
                                                                                )
                                                                            ) { append("${nutrient.name} ") }

                                                                            nutrient.symbol?.let { s ->
                                                                                append("($s) ")
                                                                            }

                                                                            append("back")
                                                                        },
                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                        fontFamily = robotoSerifFamily
                                                                    )
                                                                }
                                                            }

                                                        }
                                                    }

                                                    TextButton(
                                                        onClick = ::onSuggestMissingNutrientsToggle,
                                                        colors = ButtonDefaults.textButtonColors(
                                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                        ),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            text = if (isSuggestingMissingNutrients) "Done"
                                                            else "Suggest missing nutrients"
                                                        )
                                                    }
                                                }
                                            }

                                        else -> {}
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        TextButton(
                            onClick = ::onReport,
                            enabled = selectedReasons.isNotEmpty(),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = WhiteFont
                            )
                        ) {
                            Text(
                                text = "Report",
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                else -> {}
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

                if (originalValue != "" && originalValue != (it.textFieldValue?.text ?: "")) {
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
