package com.example.fitnessway.feature.home.screen.main.foodselection.foodlog.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodLogField

@Composable
fun FoodLogBaseInformationField(
    field: FoodLogField,
    isEditing: Boolean,
) {
    val borderWidth = if (isEditing) 1.dp else 0.dp
    val borderColor = if (isEditing) MaterialTheme.colorScheme.primary else Color.Transparent
    val borderShape = if (isEditing) 10.dp else 0.dp

    val padding = if (isEditing) 14.dp else 0.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            BasicTextField(
                value = field.value,
                onValueChange = field.updateState,
                textStyle = TextStyle(
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontFamily = FontFamily.Default,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End
                ),
                singleLine = true,
                enabled = isEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = borderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(borderShape)
                    )
                    .padding(padding)
            )
        }
    )
}