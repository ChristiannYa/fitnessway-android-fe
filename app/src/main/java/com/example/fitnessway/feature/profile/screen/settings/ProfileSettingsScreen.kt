package com.example.fitnessway.feature.profile.screen.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.ImperialRed
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                title = "Settings"
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("")

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::logout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImperialRed,
                    contentColor = WhiteFont
                )
            ) {
                Text(
                    text = "Log out",
                    fontFamily = robotoSerifFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileSettingsScreenPreview() {
    FitnesswayTheme {
        ProfileSettingsScreen(onBackClick = {})
    }
}