package com.example.fitnessway.util.form.providers

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.form.RegisterField
import com.example.fitnessway.feature.welcome.screen.register.viewmodel.RegisterViewModel

class RegisterFieldsProvider(private val viewModel: RegisterViewModel) {
    @Composable
    fun stepOne(): RegisterField {
        return RegisterField(
            name = FormFieldName.Register.NAME,
            label = "Enter your name",
            value = viewModel.name,
            updateState = {
                viewModel.updateField(
                    FormFieldName.Register.NAME,
                    it
                )
            },
        )
    }

    @Composable
    fun stepTwo(): RegisterField {
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
            keyboardType = KeyboardType.Email
        )
    }

    @Composable
    fun stepThree(): List<RegisterField> {
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
}