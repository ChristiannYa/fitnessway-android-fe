package com.example.fitnessway.feature.welcome.screen.register.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.welcome.screen.register.viewmodel.RegisterViewModel

@Composable
fun RegisterProgressIndicator(
   viewModel: RegisterViewModel
) {
   Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(
         12.dp, Alignment.CenterHorizontally
      ),
   ) {
      ProgressIndicator(
         viewModel.currentStep == 1,
         isValid = viewModel.stepOneIsValid,
         MaterialTheme.colorScheme.tertiary
      )
      ProgressIndicator(
         viewModel.currentStep == 2,
         isValid = viewModel.stepTwoIsValid,
         MaterialTheme.colorScheme.secondary
      )
      ProgressIndicator(
         viewModel.currentStep == 3,
         isValid = viewModel.stepThreeIsValid,
         MaterialTheme.colorScheme.primary
      )
   }
}

@Composable
fun ProgressIndicator(
   isCurrentStep: Boolean,
   isValid: Boolean,
   activeColor: Color
) {
   val scale by animateFloatAsState(
      targetValue = if (isCurrentStep)
         1f
      else
         0.6f,
      animationSpec = tween(durationMillis = 300),
      label = "register_form_SCALE_ANIMATION"
   )

   val color by animateColorAsState(
      targetValue = if (isValid)
         activeColor
      else
         MaterialTheme.colorScheme.surfaceVariant,
      animationSpec = tween(durationMillis = 300),
      label = "register_form_COLOR_ANIMATION"
   )

   Box(
      modifier = Modifier
         .size(16.dp)
         .scale(scale)
         .background(
            color = color,
            shape = CircleShape
         )
   )
}