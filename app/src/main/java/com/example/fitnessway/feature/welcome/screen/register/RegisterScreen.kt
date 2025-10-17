package com.example.fitnessway.feature.welcome.screen.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun RegisterScreen(
   onRegisterClick: () -> Unit,
   onBackClick: () -> Unit,
) {
   val nameState = rememberTextFieldState("Christian")

   Surface(modifier = Modifier.fillMaxSize()) {
      Column {
         Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(
               onClick = onBackClick,
               content = {
                  Icon(
                     imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                     contentDescription = "Go Back",
                     tint = MaterialTheme.colorScheme.onBackground
                  )
               }
            )
         }

         Spacer(modifier = Modifier.height(12.dp))

         Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Labels: Wrapper
            Column(
               modifier = Modifier.fillMaxSize(),
               horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.spacedBy(space = 16.dp)
            ) {
               // Name: Field
               Column(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalAlignment = Alignment.Start
               ) {
                  // Name: Label
                  Text(
                     text = "Your Name",
                     color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                     fontWeight = FontWeight.Medium,
                     fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                     fontFamily = robotoSerifFamily
                  )

                  Spacer(modifier = Modifier.height(6.dp))

                  // Name: Input
                  BasicTextField(
                     state = nameState,
                     lineLimits = TextFieldLineLimits.SingleLine,
                     textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                     ),
                     decorator = { innerTextField ->
                        Row(
                           Modifier
                              .background(
                                 color = MaterialTheme.colorScheme.surfaceVariant,
                                 shape = RoundedCornerShape(percent = 18)
                              )
                              .padding(16.dp)
                              .fillMaxWidth()
                        ) {
                           innerTextField()
                        }
                     }
                  )
               }
            }
         }
      }
   }
}

@Preview()
@Composable
fun RegisterScreenPreview() {
   FitnesswayTheme {
      RegisterScreen(
         onRegisterClick = {},
         onBackClick = {}
      )
   }
}