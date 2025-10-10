package com.example.fitnessway.feature.welcome.screen

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun WelcomeMainScreen(
   onLoginClick: () -> Unit,
   onRegisterClick: () -> Unit
) {
   val titleColor = if (isSystemInDarkTheme())
      MaterialTheme.colorScheme.onBackground else
      MaterialTheme.colorScheme.primary

   Surface(
      modifier = Modifier.fillMaxSize()
   ) {
      Column(
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            Alignment.CenterVertically
         )
      ) {
         Text(
            text = "Fitnessway",
            fontFamily = FontFamily.Serif,
            fontSize = MaterialTheme.typography.displaySmall.fontSize,
            color = titleColor
         )

         Column(
            modifier = Modifier
               // IntrinsicSize.Max makes the Column's width equal to the width of its widest child
               .width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(
               space = 4.dp,
               Alignment.CenterVertically
            ),
         ) {
            TextButton(
               onClick = onLoginClick,
               modifier = Modifier
                  .fillMaxWidth(),
               contentPadding = (PaddingValues(
                  top = 12.dp,
                  bottom = 12.dp
               )),
               colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primary,
               ),
               content = {
                  Text(
                     text = "Login",
                     fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                     fontFamily = robotoSerifFamily,
                     fontWeight = FontWeight.Normal,
                  )
               }
            )

            TextButton(
               onClick = onRegisterClick,
               modifier = Modifier
                  .fillMaxWidth()
                  .height(32.dp),
               contentPadding = (PaddingValues(
                  top = 0.dp,
                  bottom = 0.dp,
                  start = 16.dp,
                  end = 16.dp
               )),
               colors = ButtonDefaults.textButtonColors(
                  containerColor = Color.Transparent,
                  contentColor = MaterialTheme.colorScheme.primary,
               ),
               content = {
                  Text(
                     text = "Register",
                     fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                     fontFamily = robotoSerifFamily,
                     fontWeight = FontWeight.Normal,
                  )
               }
            )
         }
      }
   }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WelcomeMainScreenPreview() {
   FitnesswayTheme {
      WelcomeMainScreen(
         onLoginClick = {},
         onRegisterClick = {}
      )
   }
}