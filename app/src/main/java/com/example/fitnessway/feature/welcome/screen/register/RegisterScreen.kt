package com.example.fitnessway.feature.welcome.screen.register

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.welcome.screen.register.composables.RegisterFormControlButton
import com.example.fitnessway.feature.welcome.screen.register.composables.RegisterProgressIndicator
import com.example.fitnessway.feature.welcome.screen.register.composables.StepOne
import com.example.fitnessway.feature.welcome.screen.register.composables.StepThree
import com.example.fitnessway.feature.welcome.screen.register.composables.StepTwo
import com.example.fitnessway.feature.welcome.screen.register.viewmodel.RegisterViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.UiState
import org.koin.compose.viewmodel.koinViewModel
import com.example.fitnessway.util.form.providers.RegisterFieldsProvider

@Composable
fun RegisterScreen(onBackClick: () -> Unit) {
    val viewModel: RegisterViewModel = koinViewModel()
    val registerUiState by viewModel.registerUiState.collectAsState()
    val fields = RegisterFieldsProvider(viewModel)

    LaunchedEffect(registerUiState) {
        if (registerUiState is UiState.Success) {
            viewModel.resetRegisterState()
        }
    }

    Screen {
        Column {
            RegisterProgressIndicator(viewModel)

            Spacer(modifier = Modifier.height(18.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnimatedContent(
                        targetState = viewModel.currentStep,
                        transitionSpec = {
                            val isForward = targetState > initialState

                            /*
                            When `isForward`

                            - Left side of `togetherWith` = Enter animation for NEW content
                               - `slideInHorizontally { it }`: Slide in from the right (positive offset)
                               - `fadeIn()`: Fade in while sliding
                               - `scaleIn()`: Scale in while fading and sliding

                            - Right side of `togetherWith` = Exit animation for OLD content
                               - `slideOutHorizontally { -it }`: Slide out to the left (negative offset)
                               - `fadeOut()`: Fade out while sliding
                               - `scaleOut()`: Scale out while fading and sliding
                          */

                            if (isForward)
                                slideInHorizontally { it } + fadeIn() + scaleIn(initialScale = 0.7f) togetherWith
                                        slideOutHorizontally { -it } + fadeOut() + scaleOut(
                                    targetScale = 0.7f
                                )
                            else
                                slideInHorizontally { -it } + fadeIn() + scaleIn(initialScale = 0.7f) togetherWith
                                        slideOutHorizontally { it } + fadeOut() + scaleOut(
                                    targetScale = 0.7f
                                )
                        }
                    ) { step ->
                        when (step) {
                            1 -> StepOne(fields.stepOne())
                            2 -> StepTwo(fields.stepTwo(), viewModel.name)
                            3 -> StepThree(fields.stepThree())
                        }
                    }

                    if (registerUiState is UiState.Error) {
                        val viewableErr = "This email is already in use"
                        val errMsg = (registerUiState as UiState.Error).message.replaceFirstChar {
                            it.uppercase()
                        }

                        if (errMsg != viewableErr) ApiErrorMessage(errMsg)

                        if (errMsg == viewableErr) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                                content = {
                                    Text(
                                        text = viewableErr,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                    )
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Go back button
                    RegisterFormControlButton(
                        onClick = {
                            if (viewModel.currentStep == 1) {
                                onBackClick()
                            } else {
                                viewModel.updateStep(
                                    step = viewModel.currentStep
                                )
                            }
                        },
                        currentStep = viewModel.currentStep,
                    )

                    // Go forward button
                    RegisterFormControlButton(
                        onClick = {
                            viewModel.updateStep(
                                step = viewModel.currentStep,
                                goesBack = false
                            )
                        },
                        currentStep = viewModel.currentStep,
                        goPrevStep = false,
                        enabled = viewModel.isCurrentStepValid,
                        isRegistering = viewModel.isRegistering
                    )
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RegisterScreenPreview() {
    FitnesswayTheme {
        RegisterScreen(onBackClick = {})
    }
}