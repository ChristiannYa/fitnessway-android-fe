package com.example.fitnessway.feature.home.screen.foodselection

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme

@Composable
fun FoodSelectionScreen(
   onBackClick: () -> Unit,
) {
   Screen(
      header = {
         Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(
               onClick = onBackClick,
               content = {
                  Icon(
                     imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                     contentDescription = "Go Back",
                     tint = MaterialTheme.colorScheme.onBackground
                  )
               }
            )
         }
      },

      content = {
         Column {
            Text("This is the food selection screen")
         }
      }
   )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FoodSelectionPreview() {
   FitnesswayTheme {
      FoodSelectionScreen(onBackClick = {})
   }
}