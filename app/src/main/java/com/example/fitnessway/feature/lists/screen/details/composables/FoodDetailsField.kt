package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.fitnessway.data.model.form.FormField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.util.Ui

@Composable
fun <T : FormFieldName.IFoodEdition> FoodDetailsField(
    field: FormField<T>,
    enabled: Boolean = true,
    onRemoveNutrient: ((nutrientId: Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isNutrient = field.name is FormFieldName.FoodEdition.NutrientField

    val inputShape = Ui.InputUi.shape
    val inputColor = Ui.InputUi.getBackgroundColor()
    val inputTextStyle = Ui.InputUi.getTextStyle()

    if (field.textFieldValue != null && field.updateTextFieldValueState != null) {
        OutlinedTextField(
            value = field.textFieldValue,
            onValueChange = field.updateTextFieldValueState,
            enabled = enabled,
            label = {
                Text(
                    text = field.label,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            trailingIcon = if (isNutrient) {
                {
                    Clickables.AppIconButton(
                        size = Clickables.AppIconButtonSize.SMALL,
                        icon = Clickables.AppIconButtonSource.Vector(Icons.Default.Delete),
                        contentDescription = "Delete ${field.name.nutrient.name} from food",
                        onClick = {
                            val nutrientId = field.name.nutrient.id
                            onRemoveNutrient?.invoke(nutrientId)
                        },
                        showsClickIndication = false
                    )
                }
            } else null,
            keyboardOptions = field.keyboardOptions,
            textStyle = inputTextStyle,
            shape = inputShape,
            colors =  OutlinedTextFieldDefaults
                .colors(
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = inputColor.copy(0.6f),
                    focusedContainerColor = inputColor,
                    focusedTextColor = MaterialTheme.colorScheme.primary
                ),
            modifier = modifier
                .fillMaxWidth()
        )
    }
}