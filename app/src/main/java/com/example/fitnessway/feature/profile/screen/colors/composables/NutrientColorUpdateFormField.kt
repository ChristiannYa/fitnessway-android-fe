package com.example.fitnessway.feature.profile.screen.colors.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FormField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.Ui.InputUi

@Composable
fun NutrientColorUpdateFormField(
    field: FormField<FormFieldName.NutrientColorUpdate>,
    modifier: Modifier = Modifier
) {
    val nutrient = field.name.nutrientData.nutrient
    val textStyle = InputUi.getTextStyle(textAlign = TextAlign.Center)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Text(
                text = nutrient.name,
                style = MaterialTheme.typography.bodyMedium
            )

            BasicTextField(
                value = field.value,
                onValueChange = field.updateState,
                textStyle = textStyle,
                singleLine = true,
                modifier = Modifier
                    .width(100.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = InputUi.shape
                    )
                    .padding(InputUi.padding)
            )
        }
    )
}

