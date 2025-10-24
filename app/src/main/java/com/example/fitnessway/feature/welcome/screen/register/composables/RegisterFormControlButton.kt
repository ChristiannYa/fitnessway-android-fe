package com.example.fitnessway.feature.welcome.screen.register.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun RegisterFormControlButton(
   onClick: () -> Unit,
   currentStep: Int,
   goPrevStep: Boolean = true,
   enabled: Boolean = true,
   isRegistering: Boolean = false,
) {
   val iconBtnSize = 46
   val iconSize = (iconBtnSize / 1.6).roundToInt()

   IconButton(
      onClick = { onClick() },
      enabled = enabled && !isRegistering,
      modifier = Modifier.size(iconBtnSize.dp),
      colors = IconButtonColors(
         containerColor = if (goPrevStep) {
            MaterialTheme.colorScheme.inverseSurface
         } else {
            when (currentStep) {
               1 -> MaterialTheme.colorScheme.tertiary
               2 -> MaterialTheme.colorScheme.secondary
               3 -> MaterialTheme.colorScheme.primary
               else -> MaterialTheme.colorScheme.surface
            }
         },
         contentColor = if (goPrevStep) {
            MaterialTheme.colorScheme.inverseOnSurface
         } else {
            MaterialTheme.colorScheme.onSurface
         },
         disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
         disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(0.4f)
      ),
      content = {
         if (isRegistering && !goPrevStep) {
            CircularProgressIndicator()
         } else {
            Icon(
               imageVector = if (goPrevStep) {
                  Icons.AutoMirrored.Filled.ArrowBack
               } else {
                  if (currentStep == 3) {
                     Icons.Default.Check
                  } else {
                     Icons.AutoMirrored.Filled.ArrowForward
                  }
               },
               contentDescription = if (goPrevStep) {
                  "Go to previous step"
               } else {
                  "Go to next step"
               },
               modifier = Modifier.size(iconSize.dp)
            )
         }
      }
   )
}