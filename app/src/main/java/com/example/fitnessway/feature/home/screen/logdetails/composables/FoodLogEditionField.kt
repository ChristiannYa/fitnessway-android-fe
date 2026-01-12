package com.example.fitnessway.feature.home.screen.logdetails.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.fitnessway.util.form.field.FoodLogEditionField
import com.example.fitnessway.util.Ui.InputUi

@Composable
fun FoodLogEditionField(field: FoodLogEditionField) {
    if (field.textFieldValue != null && field.updateTextFieldValueState != null) {
        BasicTextField(
            value = field.textFieldValue,
            onValueChange = field.updateTextFieldValueState,
            keyboardActions = field.keyboardActions,
            keyboardOptions = field.keyboardOptions,
            textStyle = InputUi.getTextStyle(),
            maxLines = 1,
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .clip(InputUi.shape)
                .background(
                    color = InputUi.getBackgroundColor(),
                    shape = InputUi.shape
                )
                .padding(InputUi.padding)
        )
    }
}