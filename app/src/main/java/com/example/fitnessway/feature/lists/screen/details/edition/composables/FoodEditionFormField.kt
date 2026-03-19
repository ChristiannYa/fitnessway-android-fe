package com.example.fitnessway.feature.lists.screen.details.edition.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.feature.lists.screen.composables.NutrientDvTrailingIcon
import com.example.fitnessway.feature.lists.screen.composables.rememberNutrientDvState
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Structure.AppIconButtonSource
import com.example.fitnessway.util.INutrientDvControls
import com.example.fitnessway.util.UNutrient
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.form.field.FormField
import com.example.fitnessway.util.form.field.FormFieldName

@Composable
fun <T : FormFieldName.IFoodEdition> FoodEditionFormField(
    field: FormField<T>,
    onRemoveNutrient: ((nutrient: MNutrient.Model.Nutrient) -> Unit)? = null,
    nutrientDvControls: INutrientDvControls.NutrientDvControls? = null,
    modifier: Modifier = Modifier
) {
    val outlinedColors = Ui.InputUi.getOutlinedColors()
    val inputShape = Ui.InputUi.shape
    val inputTextStyle = Ui.InputUi.getTextStyle()

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val isNutrient = field.name is FormFieldName.FoodEdition.NutrientField
    val nutrientDvState = rememberNutrientDvState(
        field = field,
        nutrientDvControls = nutrientDvControls,
        getNutrient = { name ->
            if (name is FormFieldName.FoodEdition.NutrientField) {
                name.nutrient
            } else null
        }
    )

    if (field.textFieldValue != null && field.updateTextFieldValueState != null) {
        OutlinedTextField(
            value = field.textFieldValue,
            onValueChange = field.updateTextFieldValueState,
            enabled = field.enabled,
            label = {
                if (nutrientDvState.nutrient != null) UNutrient.Ui.NutrientFieldLabel(
                    nutrient = nutrientDvState.nutrient,
                    isFocused = isFocused,
                    isInDvMode = nutrientDvState.isInNutrientDvMap
                ) else Text(
                    text = field.label,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = if (isNutrient) {
                {
                    val nutrient = field.name.nutrient

                    Clickables.AppPngIconButton(
                        icon = AppIconButtonSource.Vector(Icons.Default.Delete),
                        contentDescription = "Delete ${nutrient.name} from food",
                        onClick = { onRemoveNutrient?.invoke(nutrient) },
                        enabled = field.enabled
                    )
                }
            } else null,
            trailingIcon = NutrientDvTrailingIcon(nutrientDvState, field.textFieldValue.text),
            keyboardOptions = field.keyboardOptions,
            keyboardActions = field.keyboardActions,
            interactionSource = interactionSource,
            textStyle = inputTextStyle,
            singleLine = true,
            shape = inputShape,
            colors = outlinedColors,
            modifier = modifier.fillMaxWidth()
        )
    }
}