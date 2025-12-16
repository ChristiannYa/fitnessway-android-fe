package com.example.fitnessway.feature.profile.screen.colors.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FormField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.Nutrient.getNutrientColor
import com.example.fitnessway.util.Ui.InputUi

@Composable
fun NutrientColorUpdateFormField(
    field: FormField<FormFieldName.NutrientColorUpdate>,
    modifier: Modifier = Modifier
) {
    val nutrient = field.name.nutrientData.nutrient
    val preferences = field.name.nutrientData.preferences
    val color = getNutrientColor(preferences.hexColor) ?: MaterialTheme.colorScheme.primary
    val textStyle = InputUi.getTextStyle(
        textAlign = TextAlign.Center
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Text(
                text = nutrient.name,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        PaddingValues(
                            start = 16.dp
                        )
                    ),
                content = {
                    BasicTextField(
                        value = field.value,
                        onValueChange = field.updateState,
                        textStyle = textStyle,
                        singleLine = true,
                        modifier = Modifier
                            .width(128.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = InputUi.shape
                            )
                            .padding(InputUi.padding)
                    )

                    val colorPreviewShape = RoundedCornerShape(5.dp)

                    Box(
                        modifier = Modifier
                            .clip(colorPreviewShape)
                            .size(18.dp)
                            .background(
                                color = color,
                                shape = colorPreviewShape
                            )
                    )
                }
            )
        }
    )
}

