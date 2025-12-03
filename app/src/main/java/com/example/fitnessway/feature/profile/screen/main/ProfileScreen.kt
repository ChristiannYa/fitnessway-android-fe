package com.example.fitnessway.feature.profile.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.R
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.UiMeasures
import org.koin.androidx.compose.koinViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ProfileScreen(
    onSettings: () -> Unit,
    onGoals: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val user = viewModel.user

    if (user == null) {
        Screen(
            content = {
                NotFoundText(text = "No user found")
            }
        )
    } else {
        Screen(
            content = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        // Profile image
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                )
                                .width(126.dp)
                                .height(126.dp),
                            content = {
                                Image(
                                    painter = painterResource(R.drawable.user_img),
                                    contentDescription = "User profile image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        )


                        // User information
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Buttons
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp)),
                            content = {
                                ProfileScreenMainButton(
                                    onClick = onGoals,
                                    imageVector = Icons.Default.FitnessCenter,
                                    text = "My Goals"
                                )

                                ProfileScreenMainButton(
                                    onClick = {},
                                    imageVector = Icons.Default.ColorLens,
                                    text = "Interface Colors"
                                )

                                ProfileScreenMainButton(
                                    onClick = {},
                                    imageVector = Icons.Default.Info,
                                    text = "Account Information"
                                )

                                ProfileScreenMainButton(
                                    onClick = onSettings,
                                    imageVector = Icons.Default.Settings,
                                    text = "Settings"
                                )
                            }
                        )
                    }
                )
            }
        )
    }
}

@Composable
fun ProfileScreenMainButton(
    text: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(UiMeasures.SCREEN_HORIZONTAL_PADDING),
        content = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
                content = {
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = robotoSerifFamily,
                        fontWeight = FontWeight.Medium,
                    )

                    Icon(
                        imageVector = imageVector,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    )
}

private fun formatDate(dateString: String): String {
    val offsetDateTime = OffsetDateTime.parse(dateString)
    val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
    return offsetDateTime.format(formatter)
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreview() {
    FitnesswayTheme {
        ProfileScreen(
            onSettings = {},
            onGoals = {}
        )
    }
}