package com.example.fitnessway.feature.profile.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.ImperialRed
import com.example.fitnessway.ui.theme.SilverMist
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun ProfileScreen(
   onSettings: () -> Unit,
) {
   Screen {
      Column {
         Text("This is the profile screen")

         Spacer(modifier = Modifier.height(16.dp))

         TextButton(
            onClick = onSettings,
            colors = ButtonDefaults.buttonColors(
               containerColor = MaterialTheme.colorScheme.surfaceVariant,
               contentColor = SilverMist
            ),
            content = {
               Text(
                  text = "Settings",
                  fontFamily = robotoSerifFamily,
                  fontWeight = FontWeight.Medium
               )
            }
         )

         Spacer(modifier = Modifier.height(16.dp))

         TextButton(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
               containerColor = ImperialRed,
               contentColor = SilverMist
            ),
            content = {
               Text(
                  text = "Log out",
                  fontFamily = robotoSerifFamily,
                  fontWeight = FontWeight.Medium
               )
            }
         )
      }
   }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreview() {
   FitnesswayTheme {
      ProfileScreen(onSettings = {})
   }
}
