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
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.R
import com.example.fitnessway.data.model.MUser.Model.User
import com.example.fitnessway.feature.profile.screen.main.composables.UpgradePromptDialog
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.PremiumIcon
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Ui.Measurements.SCREEN_HORIZONTAL_PADDING
import com.example.fitnessway.util.Ui.Measurements.TEXT_ICON_HORIZONTAL_SPACE
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

    if (user != null) {
        var isUpgradePromptDialogDisplayed by remember { mutableStateOf(false) }

        Screen {
            Box(
                modifier = Modifier.fillMaxSize(),
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            ProfileImage(
                                user = user,
                                imagePainter = painterResource(R.drawable.user_img)
                            )

                            ProfileInformation(user)

                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp)),
                            ) {
                                val buttons = getProfileScreenButtons(
                                    onNavigateToGoals = onNavigateToGoals,
                                    onNavigateToColors = {
                                        if (user.isPremium) onNavigateToColors() else {
                                            isUpgradePromptDialogDisplayed = true
                                        }
                                    },
                                    onNavigateToAccInfo = onNavigateToAccInfo,
                                    onNavigateToSettings = onNavigateToSettings
                                )

                                buttons.forEach { button ->
                                    ProfileScreenMainButton(
                                        isButtonPremium = button.isButtonPremium,
                                        text = button.text,
                                        imageVector = button.imageVector,
                                        onClick = button.onClick,
                                        isUserPremium = user.isPremium
                                    )
                                }
                            }

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
    } else Screen { NotFoundMessage("No user found") }

}

private data class ProfileButtonSettings(
    val isButtonPremium: Boolean,
    val onClick: () -> Unit,
    val imageVector: ImageVector,
    val text: String,
)

private fun getProfileScreenButtons(
    onNavigateToGoals: () -> Unit,
    onNavigateToColors: () -> Unit,
    onNavigateToAccInfo: () -> Unit,
    onNavigateToSettings: () -> Unit
): List<ProfileButtonSettings> {
    return listOf(
        ProfileButtonSettings(
            isButtonPremium = false,
            onClick = onNavigateToGoals,
            imageVector = Icons.Outlined.FitnessCenter,
            text = "My goals",
        ),
        ProfileButtonSettings(
            isButtonPremium = true,
            onClick = onNavigateToColors,
            imageVector = Icons.Outlined.ColorLens,
            text = "Color Palette"
        ),
        ProfileButtonSettings(
            isButtonPremium = false,
            onClick = onNavigateToAccInfo,
            imageVector = Icons.Outlined.AccountCircle,
            text = "Account"
        ),
        ProfileButtonSettings(
            isButtonPremium = false,
            onClick = onNavigateToSettings,
            imageVector = Icons.Outlined.Settings,
            text = "Settings"
        ),
    )
}

@Composable
private fun ProfileScreenMainButton(
    text: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
    isButtonPremium: Boolean,
    isUserPremium: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(SCREEN_HORIZONTAL_PADDING)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TEXT_ICON_HORIZONTAL_SPACE),
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = text,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = robotoSerifFamily,
                    fontWeight = FontWeight.Medium,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(TEXT_ICON_HORIZONTAL_SPACE),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isButtonPremium && !isUserPremium) PremiumIcon()

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                    contentDescription = "Go to $text",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ProfileInformation(user: User) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = user.name,
            style = MaterialTheme.typography.bodyLarge
        )

        val premiumStatusText = if (user.isPremium) "Premium Account" else "Free Account"

        Text(
            text = premiumStatusText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileImage(
    user: User,
    imagePainter: Painter
) {
    Box(
        modifier = Modifier
            .size(120.dp),
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
                        painter = imagePainter,
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
}