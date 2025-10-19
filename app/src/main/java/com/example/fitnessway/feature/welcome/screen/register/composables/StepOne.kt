package com.example.fitnessway.feature.welcome.screen.register.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.fitnessway.data.model.welcome.FormFieldName
import com.example.fitnessway.data.model.welcome.RegisterField
import com.example.fitnessway.ui.theme.FitnesswayTheme

@Composable
fun StepOne(field: RegisterField) {
   Surface {
      Column(
         verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
         Text(
            text = buildAnnotatedString {
               append("Welcome to ")
               withStyle(
                  style = SpanStyle(
                     fontWeight = FontWeight.SemiBold
                  )
               ) {
                  append("Fitnessway")
               }
               append("! How should we call you?")
            },
            fontFamily = FontFamily.Serif,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Medium,
            style = LocalTextStyle.current.merge(
               TextStyle(
                  lineHeight = 1.4.em,
                  platformStyle = PlatformTextStyle(
                     includeFontPadding = false
                  ),
                  lineHeightStyle = LineHeightStyle(
                     alignment = LineHeightStyle.Alignment.Center,
                     trim = LineHeightStyle.Trim.None
                  )
               )
            )
         )

         RegisterTextField(field)
      }
   }
}

@Preview(
   showBackground = true,
   uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun PreviewStepOne() {
   FitnesswayTheme {
      StepOne(
         RegisterField(
            name = FormFieldName.Register.NAME,
            label = "Enter your name",
            value = "Christian",
            updateState = {},
            errorMessage = null
         )
      )
   }
}
