package com.example.fitnessway.feature.home.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.SilverMist
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun HomeScreen(
   onFoodSelection: () -> Unit,
) {
   Screen {
      Column {
         Text("This is the home screen")

         TextButton(
            onClick = onFoodSelection,
            colors = ButtonDefaults.buttonColors(
               containerColor = MaterialTheme.colorScheme.primary,
               contentColor = SilverMist
            ),
            content = {
               Text(
                  text = "Log",
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
fun HomeScreenPreview() {
   FitnesswayTheme {
      HomeScreen(onFoodSelection = {})
   }
}