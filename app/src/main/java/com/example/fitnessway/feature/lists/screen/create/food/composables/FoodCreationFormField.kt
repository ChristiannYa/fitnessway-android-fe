package com.example.fitnessway.feature.lists.screen.create.food.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FormField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.Ui

@Composable
fun <T : FormFieldName.IFoodCreation> FoodCreationFormField(
    field: FormField<T>,
    enabled: Boolean = true
) {
    // val isNutrient = field.name is FormFieldName.FoodCreation.NutrientField

    val inputShape = Ui.InputUi.shape
    val inputColor = Ui.InputUi.getColor()

    OutlinedTextField(
        label = {
            Text(
                text = field.label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(2.dp)
            )
        },
        supportingText = if (field.errorMessage != null) {
            {
                Text(
                    text = field.errorMessage,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 2.dp)
                )
            }
        } else {
            null
        },
        value = field.value,
        onValueChange = field.updateState,
        enabled = enabled,
        keyboardOptions = field.keyboardOptions,
        textStyle = TextStyle(
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            fontFamily = FontFamily.Default,
        ),
        singleLine = true,
        shape = inputShape,
        colors = OutlinedTextFieldDefaults
            .colors(
                unfocusedBorderColor = Color.Transparent,
                unfocusedContainerColor = inputColor.copy(0.6f),
                focusedContainerColor = inputColor,
                focusedTextColor = MaterialTheme.colorScheme.primary
            ),
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (field.focusRequester != null) {
                    Modifier.focusRequester(field.focusRequester)
                } else Modifier
            )
    )
}