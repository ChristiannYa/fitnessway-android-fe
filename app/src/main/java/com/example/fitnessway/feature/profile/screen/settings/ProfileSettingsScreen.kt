package com.example.fitnessway.feature.profile.screen.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitnessway.feature.profile.screen.settings.viewmodel.ProfileSettingsScreenViewModel
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.ImperialRed
import com.example.fitnessway.ui.theme.SilverMist
import com.example.fitnessway.ui.theme.robotoSerifFamily
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.example.fitnessway.util.UiState

@Composable
fun ProfileSettingsScreen(
   onBackClick: () -> Unit,
) {
   val viewModel: ProfileSettingsScreenViewModel = koinViewModel()
   val profileSettingsUiState by viewModel.profileUiState.collectAsState()

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
         Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
         ) {
            Text("This is the profile settings screen")

            TextButton(
               modifier = Modifier.fillMaxWidth(),
               onClick = { viewModel.logout() },
               colors = ButtonDefaults.buttonColors(
                  containerColor = ImperialRed,
                  contentColor = SilverMist
               ),
               content = {
                  if (profileSettingsUiState is UiState.Loading) {
                     CircularProgressIndicator(
                        modifier = Modifier
                           .size(18.dp),
                        color = SilverMist,
                        strokeWidth = 1.dp
                     )
                  } else {
                     Text(
                        text = "Log out",
                        fontFamily = robotoSerifFamily,
                        fontWeight = FontWeight.Medium
                     )
                  }
               }
            )
         }
      }
   )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileSettingsScreenPreview() {
   FitnesswayTheme {
      ProfileSettingsScreen(onBackClick = {})
   }
}