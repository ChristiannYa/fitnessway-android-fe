package com.example.fitnessway.feature.welcome.screen.register.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.form.RegisterField

@Composable
fun StepThree(fields: List<RegisterField>) {
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordConfirmVisible by remember { mutableStateOf(false) }

    Surface {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Finally, provide a secure password for your account",
                fontFamily = FontFamily.Serif,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Medium,
                style = LocalTextStyle.current.merge(
                    TextStyle(
                        lineHeight = 1.4.em,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        )
                    )
                )
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                fields.forEach { field ->
                    val isPasswordVisible = when (field.name) {
                        FormFieldName.Register.PASSWORD -> passwordVisible
                        FormFieldName.Register.CONFIRM_PASSWORD -> passwordConfirmVisible
                        else -> false
                    }

                    RegisterTextField(
                        field,
                        isPasswordVisible = isPasswordVisible,
                        onPasswordVisibilityToggle = {
                            when (field.name) {
                                FormFieldName.Register.PASSWORD ->
                                    passwordVisible = !passwordVisible

                                FormFieldName.Register.CONFIRM_PASSWORD ->
                                    passwordConfirmVisible = !passwordConfirmVisible

                                else -> { /* do nothing */
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
