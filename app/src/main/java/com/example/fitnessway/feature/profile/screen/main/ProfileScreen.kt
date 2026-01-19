package com.example.fitnessway.feature.profile.screen.main

import android.view.SoundEffectConstants
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MUser.Model.User
import com.example.fitnessway.feature.profile.screen.main.composables.UpgradePromptDialog
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.Loading.RefreshByPullIndicator
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.ui.shared.PremiumIcon
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Formatters.formatUiErrorMessage
import com.example.fitnessway.util.Ui.Measurements.SCREEN_HORIZONTAL_PADDING
import com.example.fitnessway.util.Ui.Measurements.TEXT_ICON_HORIZONTAL_SPACE
import com.example.fitnessway.util.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    onNavigateToGoals: () -> Unit,
    onNavigateToColors: () -> Unit,
    onNavigateToAccInfo: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()
    val userFlow by viewModel.userFlow.collectAsState()

    val user = userFlow
    val nutrientsUiState = nutrientRepoUiState.nutrientsUiState

    LaunchedEffect(Unit) {
        viewModel.getNutrients()
    }

    val scrollState = rememberScrollState()
    val pullToRefreshState = rememberPullToRefreshState()
    val pullToRefreshThresholdReached = pullToRefreshState.distanceFraction >= 1f
    val isRefreshing = pullToRefreshThresholdReached && nutrientsUiState is UiState.Loading

    var hasRefreshed by remember { mutableStateOf(false) }
    var isUpgradePromptDialogDisplayed by remember { mutableStateOf(false) }

    if (user != null) {
        Screen {
            Box(modifier = Modifier.fillMaxSize()) {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        if (!hasRefreshed) hasRefreshed = true
                        viewModel.refreshNutrients()
                    },
                    state = pullToRefreshState,
                    modifier = Modifier.fillMaxSize(),
                    indicator = {
                        RefreshByPullIndicator(
                            isRefreshing = isRefreshing,
                            state = pullToRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                ) {
                    if (nutrientsUiState is UiState.Loading && !hasRefreshed) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        NotFoundMessageAnimated(
                            isVisible = nutrientsUiState is UiState.Error,
                            message = formatUiErrorMessage(nutrientsUiState)
                        )

                        ProfileImage(user = user) {
                            Text(
                                text = "${user.name.first()}",
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.surfaceTint
                            )

                            /*
                            Image(
                                painter = painterResource(R.drawable.user_img),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )

                             */
                        }

                        ProfileInformation(user)

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                        ) {
                            if (nutrientsUiState is UiState.Success) {
                                ProfileScreenMainButton(
                                    text = "My Goals",
                                    imageVector = Icons.Outlined.FitnessCenter,
                                    onClick = {
                                        viewModel.initNutrientGoalsForm(nutrientsUiState.data)
                                        onNavigateToGoals()
                                    },
                                    isButtonPremium = false,
                                    isUserPremium = user.isPremium,
                                )

                                ProfileScreenMainButton(
                                    text = "Color Palette",
                                    imageVector = Icons.Outlined.Palette,
                                    onClick = {
                                        viewModel.initNutrientColorsForm(nutrientsUiState.data)
                                        onNavigateToColors()
                                    },
                                    isButtonPremium = false,
                                    isUserPremium = user.isPremium
                                )

                            }

                            ProfileScreenMainButton(
                                text = "Account",
                                imageVector = Icons.Outlined.AccountCircle,
                                onClick = onNavigateToAccInfo,
                                isUserPremium = user.isPremium
                            )

                            ProfileScreenMainButton(
                                text = "Settings",
                                imageVector = Icons.Outlined.Settings,
                                onClick = onNavigateToSettings,
                                isUserPremium = user.isPremium
                            )
                        }

                        if (isUpgradePromptDialogDisplayed) {
                            UpgradePromptDialog(
                                onDismiss = { isUpgradePromptDialogDisplayed = false },
                                onUpgradeClick = {}
                            )
                        }
                    }
                }
            }
        }
    } else NotFoundScreen(message = "user not found")

}

@Composable
private fun ProfileScreenMainButton(
    text: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
    isButtonPremium: Boolean = false,
    isUserPremium: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    onClick()
                },
            )
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(SCREEN_HORIZONTAL_PADDING.times(1.2f))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TEXT_ICON_HORIZONTAL_SPACE),
            ) {
                Structure.AppIconDynamic(
                    source = Structure.AppIconButtonSource.Vector(imageVector),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = robotoSerifFamily,
                    fontWeight = FontWeight.Medium,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(TEXT_ICON_HORIZONTAL_SPACE),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isButtonPremium && !isUserPremium) PremiumIcon(size = 18.dp)

                Structure.AppIconDynamic(
                    source = Structure.AppIconButtonSource.Vector(Icons.Default.ChevronRight),
                    modifier = Modifier.size(18.dp)
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
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileImage(
    user: User,
    content: @Composable () -> Unit,
) {
    Box(modifier = Modifier.size(100.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
        ) { content() }

        if (user.isPremium) {
            PremiumIcon(
                size = 20.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 2.dp)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    )
                    .padding(6.dp)
            )
        }
    }
}