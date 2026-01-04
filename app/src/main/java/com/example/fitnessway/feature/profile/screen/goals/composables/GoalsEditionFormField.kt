package com.example.fitnessway.feature.profile.screen.goals.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.fitnessway.data.model.form.FormField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.UNutrient.Ui.NutrientFieldLabel
import com.example.fitnessway.util.UNutrient.getUserNutrientColor
import com.example.fitnessway.util.Ui.InputUi

@Composable
fun GoalsEditionFormField(
    field: FormField<FormFieldName.NutrientGoalData>,
    isUserPremium: Boolean,
    modifier: Modifier = Modifier
) {
    val nutrient = field.name.nutrientData.nutrient
    val preferences = field.name.nutrientData.preferences

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val goalTextColor = getUserNutrientColor(
        color = preferences.hexColor,
        isUserPremium = isUserPremium
    )

    val inputShape = InputUi.shape
    val inputTextStyle = InputUi.getTextStyle(textColor = goalTextColor)
    val inputColors = InputUi.getOutlinedColors()

    if (field.textFieldValue != null && field.updateTextFieldValueState != null) {
        OutlinedTextField(
            value = field.textFieldValue,
            onValueChange = field.updateTextFieldValueState,
            enabled = field.enabled,
            label = {
                NutrientFieldLabel(
                    nutrient = nutrient,
                    isFocused = isFocused
                )
            },
            interactionSource = interactionSource,
            keyboardOptions = field.keyboardOptions,
            keyboardActions = field.keyboardActions,
            textStyle = inputTextStyle,
            singleLine = true,
            shape = inputShape,
            colors = inputColors,
            modifier = modifier.fillMaxWidth()
        )
    }
}