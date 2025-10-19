package com.example.fitnessway.feature.welcome.screen.register

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessway.R
import com.example.fitnessway.data.model.welcome.FormFieldName
import com.example.fitnessway.data.model.welcome.RegisterField
import com.example.fitnessway.feature.welcome.screen.register.viewmodel.RegisterViewModel
import com.example.fitnessway.ui.shared.ScreenWithHeader
import com.example.fitnessway.ui.theme.FitnesswayTheme

@Composable
fun RegisterScreen(
   onRegisterClick: () -> Unit,
   onBackClick: () -> Unit,
) {
   val registerViewModel: RegisterViewModel = viewModel<RegisterViewModel>()
   var passwordVisible by remember { mutableStateOf(false) }
   var passwordConfirmVisible by remember { mutableStateOf(false) }

   val fields: List<RegisterField> = listOf(
      RegisterField(
         name = FormFieldName.Register.NAME,
         label = "Name",
         value = registerViewModel.name,
         updateState = {
            registerViewModel.updateField(
               FormFieldName.Register.NAME, it
            )
         },
         errorMessage = registerViewModel.nameError
      ),
      RegisterField(
         name = FormFieldName.Register.EMAIL,
         label = "Email Address",
         value = registerViewModel.email,
         updateState = {
            registerViewModel.updateField(
               FormFieldName.Register.EMAIL, it
            )
         },
         errorMessage = registerViewModel.emailError,
         keyboardType = KeyboardType.Email,
      ),
      RegisterField(
         name = FormFieldName.Register.PASSWORD,
         label = "Password",
         value = registerViewModel.password,
         updateState = {
            registerViewModel.updateField(
               FormFieldName.Register.PASSWORD, it
            )
         },
         errorMessage = registerViewModel.passwordError,
         keyboardType = KeyboardType.Password,
      ),
      RegisterField(
         name = FormFieldName.Register.CONFIRM_PASSWORD,
         label = "Confirm Password",
         value = registerViewModel.confirmPassword,
         updateState = {
            registerViewModel.updateField(
               FormFieldName.Register.CONFIRM_PASSWORD, it
            )
         },
         errorMessage = registerViewModel.confirmPasswordError,
         keyboardType = KeyboardType.Password,
      ),
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
         fields.forEach { field ->
            val isPasswordVisible = when (field.name) {
               FormFieldName.Register.PASSWORD -> passwordVisible
               FormFieldName.Register.CONFIRM_PASSWORD -> passwordConfirmVisible
               else -> false // False for the rest of the fields
            }

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
                  if (field.keyboardType == KeyboardType.Password) {
                     IconButton(
                        onClick = {
                           when (field.name) {
                              FormFieldName.Register.PASSWORD ->
                                 passwordVisible = !passwordVisible

                              FormFieldName.Register.CONFIRM_PASSWORD ->
                                 passwordConfirmVisible = !passwordConfirmVisible

                              else -> {}
                           }
                        },
                        content = {
                           Icon(
                              painter = painterResource(
                                 if (isPasswordVisible)
                                    R.drawable.eye_on
                                 else
                                    R.drawable.eye_off
                              ),
                              contentDescription = if (isPasswordVisible)
                                 "Hide password"
                              else
                                 "Show password",
                              tint = MaterialTheme.colorScheme.onBackground,
                              modifier = Modifier.size(21.dp)
                           )
                        }
                     )
                  }
               },
               isError = field.errorMessage != null,
               supportingText = {
                  field.errorMessage?.let { Text(text = it) }
               },
               visualTransformation = if (isPasswordVisible)
                  VisualTransformation.None
               else
                  PasswordVisualTransformation(),
               keyboardOptions = KeyboardOptions(
                  keyboardType = field.keyboardType,
                  capitalization = field.autoCapitalize
               ),
               modifier = Modifier.fillMaxWidth(),
               shape = RoundedCornerShape(8.dp)
            )
         }
      }
   )
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