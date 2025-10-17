package com.example.fitnessway.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScreenWithHeader(
   header: (@Composable () -> Unit)? = null,
   content: @Composable () -> Unit,
) {
   Surface(
      modifier = Modifier.fillMaxSize(),
   ) {
      Box(
         modifier = Modifier.padding(horizontal = 16.dp)
      ) {
         Column {
            header?.let {
               it()
               Spacer(modifier = Modifier.height(10.dp))
            }

            content()
         }
      }
   }
}