package com.example.fitnessway.feature.profile.screen.colors.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FormField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.UNutrient.Ui.NutrientFieldLabel
import com.example.fitnessway.util.Ui.InputUi

@Composable
fun NutrientColorUpdateFormField(
    field: FormField<FormFieldName.NutrientColorUpdate>,
    modifier: Modifier = Modifier
) {
    val nutrient = field.name.nutrientData.nutrient

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val inputBorderColor = InputUi.getBorderColorAnimated(
        isFocused = isFocused,
        label = "NutrientColorFormField"
    )
    val inputBackgroundColor = InputUi.getBackgroundColorAnimated(
        isFocused = isFocused,
        isEnabled = field.enabled,
        label = "NutrientColorFormField"
    )
    val inputTextStyle = InputUi.getTextStyle(textAlign = TextAlign.Center)
    val inputPadding = InputUi.padding
    val inputShape = InputUi.shape

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        NutrientFieldLabel(
            nutrient = nutrient,
            isFocused = isFocused
        )

        BasicTextField(
            value = field.value,
            onValueChange = field.updateState,
            enabled = field.enabled,
            textStyle = inputTextStyle,
            singleLine = true,
            interactionSource = interactionSource,
            keyboardOptions = field.keyboardOptions,
            keyboardActions = field.keyboardActions,
            cursorBrush = SolidColor(inputTextStyle.color), // Caret color
            modifier = Modifier
                .clip(inputShape)
                .width(120.dp)
                .background(
                    color = inputBackgroundColor,
                    shape = inputShape
                )
                .border(
                    width = 2.dp,
                    color = inputBorderColor,
                    shape = inputShape
                )
        ) { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(inputPadding)
            ) {
                innerTextField()
            }
        }
    }

}

