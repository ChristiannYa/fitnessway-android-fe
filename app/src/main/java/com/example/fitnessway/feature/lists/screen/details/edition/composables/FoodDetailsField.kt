package com.example.fitnessway.feature.lists.screen.details.edition.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fitnessway.data.model.form.FormField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.util.Ui

@Composable
fun <T : FormFieldName.IFoodEdition> FoodDetailsField(
    field: FormField<T>,
    onRemoveNutrient: ((nutrientId: Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isNutrient = field.name is FormFieldName.FoodEdition.NutrientField

    val outlinedColors = Ui.InputUi.getOutlinedColors()
    val inputShape = Ui.InputUi.shape
    val inputTextStyle = Ui.InputUi.getTextStyle()

    if (field.textFieldValue != null && field.updateTextFieldValueState != null) {
        OutlinedTextField(
            value = field.textFieldValue,
            onValueChange = field.updateTextFieldValueState,
            enabled = field.enabled,
            label = {
                Text(
                    text = field.label,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            trailingIcon = if (isNutrient) {
                {
                    val nutrient = field.name.nutrient

                    Clickables.AppIconButton(
                        size = Clickables.AppIconButtonSize.SMALL,
                        icon = Clickables.AppIconButtonSource.Vector(Icons.Default.Delete),
                        contentDescription = "Delete ${nutrient.name} from food",
                        onClick = {
                            onRemoveNutrient?.invoke(nutrient.id)
                        },
                        enabled = field.enabled,
                        showsClickIndication = false
                    )
                }
            } else null,
            keyboardOptions = field.keyboardOptions,
            keyboardActions = field.keyboardActions,
            textStyle = inputTextStyle,
            singleLine = true,
            shape = inputShape,
            colors = outlinedColors,
            modifier = modifier.fillMaxWidth()
        )
    }
}