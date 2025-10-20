package com.example.fitnessway.feature.welcome.screen.register

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessway.data.model.welcome.register.RegisterFieldsProvider
import com.example.fitnessway.feature.welcome.screen.register.composables.RegisterFormControlButton
import com.example.fitnessway.feature.welcome.screen.register.composables.RegisterProgressIndicator
import com.example.fitnessway.feature.welcome.screen.register.composables.StepOne
import com.example.fitnessway.feature.welcome.screen.register.composables.StepThree
import com.example.fitnessway.feature.welcome.screen.register.composables.StepTwo
import com.example.fitnessway.feature.welcome.screen.register.viewmodel.RegisterViewModel
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme

@Composable
fun RegisterScreen(
   onRegisterClick: () -> Unit,
   onBackClick: () -> Unit,
) {
   val viewModel: RegisterViewModel = viewModel<RegisterViewModel>()
   val fields = RegisterFieldsProvider(viewModel)

   Log.d("RegisterScreen", "current step: ${viewModel.currentStep}")

   Screen {
      Column {
         RegisterProgressIndicator(viewModel)

         Spacer(modifier = Modifier.height(18.dp))

         Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
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
                        slideOutHorizontally { -it } + fadeOut() + scaleOut(targetScale = 0.7f)
                  else
                     slideInHorizontally { -it } + fadeIn() + scaleIn(initialScale = 0.7f) togetherWith
                        slideOutHorizontally { it } + fadeOut() + scaleOut(targetScale = 0.7f)
               }
            ) { step ->
               when (step) {
                  1 -> StepOne(fields.stepOne())
                  2 -> StepTwo(fields.stepTwo(), viewModel.name)
                  3 -> StepThree(fields.stepThree())
               }
            }

            Row(
               modifier = Modifier.fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween
            ) {
               RegisterFormControlButton(
                  viewModel = viewModel,
                  onBackClick = onBackClick,
                  goPrevStep = true
               )

               RegisterFormControlButton(
                  viewModel = viewModel,
                  onBackClick = onBackClick,
                  goPrevStep = false
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
      RegisterScreen(
         onRegisterClick = {},
         onBackClick = {}
      )
   }
}