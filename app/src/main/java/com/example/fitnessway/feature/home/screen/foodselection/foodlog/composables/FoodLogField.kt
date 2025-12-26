package com.example.fitnessway.feature.home.screen.foodselection.foodlog.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodLogField
import com.example.fitnessway.util.Ui.InputUi

@Composable
fun FoodLogField(field: FoodLogField) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = field.label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(end = 16.dp)
        )

        BasicTextField(
            value = field.value,
            keyboardOptions = field.keyboardOptions,
            keyboardActions = field.keyboardActions,
            onValueChange = field.updateState,
            textStyle = InputUi.getTextStyle(),
            maxLines = 1,
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .clip(InputUi.shape)
                .background(
                    color = InputUi.getColor(),
                    shape = InputUi.shape
                )
                .padding(InputUi.padding)
        )
    }
}