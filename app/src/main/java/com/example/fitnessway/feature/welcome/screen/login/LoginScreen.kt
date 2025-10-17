package com.example.fitnessway.feature.welcome.screen.login

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessway.data.model.welcome.LoginField
import com.example.fitnessway.feature.welcome.screen.login.viewmodel.LoginViewModel
import com.example.fitnessway.ui.shared.ScreenWithHeader
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource

@Composable
fun LoginScreen(
   onLoginClick: () -> Unit,
   onBackClick: () -> Unit,
) {
   val loginViewModel: LoginViewModel = viewModel<LoginViewModel>()
   var passwordVisible by remember { mutableStateOf(false) }

   val fields = listOf(
      LoginField(
         label = "Email Address",
         value = loginViewModel.email,
         updateState = { loginViewModel.updateEmail(it) },
         keyboardType = KeyboardType.Email,
         autoCapitalize = KeyboardCapitalization.None,
         errorMessage = if (loginViewModel.emailHasErrors)
            "Invalid email format"
         else
            null
      ),
      LoginField(
         label = "Password",
         value = loginViewModel.password,
         keyboardType = KeyboardType.Password,
         updateState = { loginViewModel.updatePassword(it) },
         isSecureTextEntry = true,
         errorMessage = if (loginViewModel.passwordHasErrors)
            "Password must have at least 8 characters"
         else
            null
      )
   )

   ScreenWithHeader(
      header = {
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
      },

      content = {
         Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
         ) {
            Column(
               verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
               fields.forEach { field ->
                  OutlinedTextField(
                     value = field.value,
                     onValueChange = { field.updateState(it) },
                     label = {
                        Text(
                           text = field.label,
                           fontFamily = FontFamily.Serif
                        )
                     },
                     trailingIcon = {
                        if (field.isSecureTextEntry) {
                           IconButton(onClick = { passwordVisible = !passwordVisible }) {
                              Icon(
                                 painter = painterResource(
                                    if (passwordVisible)
                                       R.drawable.eye_on
                                    else
                                       R.drawable.eye_off
                                 ),
                                 contentDescription = if (passwordVisible)
                                    "Hide password"
                                 else
                                    "Show password",
                                 tint = MaterialTheme.colorScheme.onBackground,
                                 modifier = Modifier
                                    .size(21.dp)
                              )
                           }
                        }
                     },
                     isError = field.errorMessage != null,
                     supportingText = {
                        field.errorMessage?.let { Text(text = it) }
                     },
                     visualTransformation = if (field.isSecureTextEntry && !passwordVisible)
                        PasswordVisualTransformation()
                     else
                        VisualTransformation.None
                     ,
                     keyboardOptions = KeyboardOptions(
                        keyboardType = field.keyboardType,
                        capitalization = field.autoCapitalize
                     ),
                     modifier = Modifier.fillMaxWidth(),
                     shape = RoundedCornerShape(8.dp)
                  )
               }
            }

            TextButton(
               onClick = { onLoginClick },
               enabled = loginViewModel.isFormValid,
               modifier = Modifier
                  .fillMaxWidth(),
               colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primary
               ),
               contentPadding = (PaddingValues(
                  top = 12.dp,
                  bottom = 12.dp
               )),
               content = {
                  Row(
                     horizontalArrangement = Arrangement.spacedBy(8.dp),
                     verticalAlignment = Alignment.CenterVertically
                  ) {
                     Icon(
                        imageVector = Icons.AutoMirrored.Filled.Login,
                        contentDescription = "Login",
                        modifier = Modifier.size(21.dp),
                        tint = if (loginViewModel.isFormValid)
                           WhiteFont
                        else
                           MaterialTheme.colorScheme.onBackground.copy(0.3f)
                     )
                     Text(
                        text = "Login",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = robotoSerifFamily
                     )
                  }
               }
            )
         }
      }
   )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenPreview() {
   FitnesswayTheme {
      LoginScreen(
         onLoginClick = {},
         onBackClick = {}
      )
   }
}