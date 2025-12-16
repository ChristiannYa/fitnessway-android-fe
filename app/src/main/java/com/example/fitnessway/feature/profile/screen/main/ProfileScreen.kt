package com.example.fitnessway.feature.profile.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.R
import com.example.fitnessway.feature.profile.screen.main.composables.UpgradePromptDialog
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.PremiumIcon
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.UiMeasures
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    onNavigateToGoals: () -> Unit,
    onNavigateToColors: () -> Unit,
    onNavigateToAccInfo: () -> Unit,
    onNavigateToSettings: () -> Unit,
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
        var isUpgradePromptDialogDisplayed by remember { mutableStateOf(false) }

        Screen(
            content = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            content = {
                                // Profile image
                                Box(
                                    modifier = Modifier
                                        .size(126.dp),
                                    content = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .background(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    shape = CircleShape
                                                ),
                                            content = {
                                                Image(
                                                    painter = painterResource(R.drawable.user_img),
                                                    contentDescription = "User profile image",
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        )

                                        if (user.isPremium) {
                                            PremiumIcon(
                                                size = 18.dp,
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = (-4).dp, y = 2.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        color = MaterialTheme.colorScheme.background,
                                                        shape = CircleShape
                                                    )
                                                    .padding(5.dp)
                                            )
                                        }
                                    }
                                )

                                // User information
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    content = {
                                        Text(
                                            text = user.name,
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        val premiumStatusText =
                                            if (user.isPremium) "Premium Account" else "Free Account"

                                        Text(
                                            text = premiumStatusText,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                )

                                // Buttons
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp)),
                                    content = {
                                        ProfileScreenMainButton(
                                            onClick = onNavigateToGoals,
                                            imageVector = Icons.Default.FitnessCenter,
                                            text = "My Goals"
                                        )

                                        ProfileScreenMainButton(
                                            onClick = {
                                                if (user.isPremium) onNavigateToColors() else isUpgradePromptDialogDisplayed =
                                                    true
                                            },
                                            imageVector = Icons.Default.ColorLens,
                                            text = "Color Palette"
                                        )

                                        ProfileScreenMainButton(
                                            onClick = onNavigateToAccInfo,
                                            imageVector = Icons.Default.Person,
                                            text = "Account Information"
                                        )

                                        ProfileScreenMainButton(
                                            onClick = onNavigateToSettings,
                                            imageVector = Icons.Default.Settings,
                                            text = "Settings"
                                        )
                                    }
                                )

                                if (isUpgradePromptDialogDisplayed) {
                                    UpgradePromptDialog(
                                        onDismiss = { isUpgradePromptDialogDisplayed = false },
                                        onUpgradeClick = {}
                                    )
                                }
                            }
                        )
                    }
                )
            }
        )
    }
}

@Composable
private fun ProfileScreenMainButton(
    text: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.primaryContainer)
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
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = robotoSerifFamily,
                        fontWeight = FontWeight.Medium,
                    )

                    Icon(
                        imageVector = imageVector,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            )
        }
    )
}