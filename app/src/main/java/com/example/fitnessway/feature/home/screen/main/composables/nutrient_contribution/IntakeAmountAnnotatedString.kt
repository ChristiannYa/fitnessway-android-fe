package com.example.fitnessway.feature.home.screen.main.composables.nutrient_contribution

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.fitnessway.util.extensions.toTruncatedDecimalString

@Composable
fun amountAnnotatedString(
    amount: Double,
    type: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) = buildAnnotatedString {

    append(amount.toTruncatedDecimalString(4))

    withStyle(
        style = SpanStyle(
            fontWeight = FontWeight.Normal,
            color = color.copy(0.6f)
        )
    ) {
        append(" $type")
    }
}