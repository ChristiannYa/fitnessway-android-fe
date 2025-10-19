package com.example.fitnessway.feature.welcome.screen.register.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessway.data.model.welcome.FormField
import com.example.fitnessway.data.model.welcome.FormFieldName
import com.example.fitnessway.data.model.welcome.RegisterField
import com.example.fitnessway.feature.welcome.screen.register.viewmodel.RegisterViewModel

@Composable
fun RegisterScreenNew(
   onRegisterClick: () -> Unit,
   onBackClick: () -> Unit,
) {
   val viewModel: RegisterViewModel = viewModel<RegisterViewModel>()


}

@Composable
private fun getStepOneField(
   viewModel: RegisterViewModel
): RegisterField {
   return RegisterField(
      name = FormFieldName.Register.NAME,
      label = "Enter your name",
      value = viewModel.name,
      updateState = {
         viewModel.updateField(
            FormFieldName.Register.NAME,
            it
         )
      }
   )
}

@Composable
private fun getStepTwoField(
   viewModel: RegisterViewModel
): RegisterField {
   return RegisterField(
      name = FormFieldName.Register.EMAIL,
      label = "Enter your email address",
      value = viewModel.email,
      updateState = {
         viewModel.updateField(
            FormFieldName.Register.EMAIL,
            it
         )
      },
      keyboardType = KeyboardType.Email,
      errorMessage = viewModel.emailError
   )
}

@Composable
private fun getStepThreeFields(
   viewModel: RegisterViewModel
): List<RegisterField> {
   return listOf(
      RegisterField(
         name = FormFieldName.Register.PASSWORD,
         label = "Password",
         value = viewModel.password,
         updateState = {
            viewModel.updateField(
               FormFieldName.Register.PASSWORD,
               it
            )
         },
         errorMessage = viewModel.passwordError,
         keyboardType = KeyboardType.Password,
      ),
      RegisterField(
         name = FormFieldName.Register.CONFIRM_PASSWORD,
         label = "Confirm Password",
         value = viewModel.confirmPassword,
         updateState = {
            viewModel.updateField(
               FormFieldName.Register.CONFIRM_PASSWORD,
               it
            )
         },
         errorMessage = viewModel.confirmPasswordError,
         keyboardType = KeyboardType.Password,
      ),
   )
}