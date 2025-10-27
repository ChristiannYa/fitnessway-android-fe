package com.example.fitnessway.feature.welcome.screen.login

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.R
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.form.LoginField
import com.example.fitnessway.feature.welcome.screen.login.viewmodel.LoginViewModel
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.SilverMist
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(onBackClick: () -> Unit) {
    val viewModel: LoginViewModel = koinViewModel()
    val loginUiState by viewModel.loginUiState.collectAsState()
    val fields = getLoginFields(viewModel)

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(loginUiState) {
        if (loginUiState is UiState.Success) {
            viewModel.resetLoginState()
        }
    }

    Screen(
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
                // Fields + Error message
                Column {
                    // Fields
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        fields.forEach { field ->
                            OutlinedTextField(
                                value = field.value,
                                onValueChange = { field.updateState(it) },
                                label = {
                                    Text(
                                        text = field.label,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = MaterialTheme.typography.labelLarge.fontSize
                                    )
                                },
                                supportingText = {
                                    field.errorMessage?.let { Text(text = it) }
                                },
                                trailingIcon = {
                                    if (field.keyboardType == KeyboardType.Password) {
                                        IconButton(onClick = {
                                            passwordVisible = !passwordVisible
                                        }) {
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
                                visualTransformation = if (field.keyboardType == KeyboardType.Password
                                    && !passwordVisible
                                )
                                    PasswordVisualTransformation()
                                else
                                    VisualTransformation.None,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = field.keyboardType,
                                    capitalization = field.autoCapitalize
                                ),
                                enabled = loginUiState !is UiState.Loading,
                                isError = field.errorMessage != null,
                                textStyle = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    if (loginUiState is UiState.Error) {
                        val viewableErr = "Invalid email or password"
                        val errMsg = (loginUiState as UiState.Error).message.replaceFirstChar {
                            it.uppercase()
                        }

                        if (errMsg != viewableErr) ApiErrorMessage(errMsg)

                        if (errMsg == viewableErr) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = viewableErr,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                )
                            }
                        }
                    }
                }

                TextButton(
                    onClick = { viewModel.login() },
                    enabled = viewModel.isFormValid,
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
                        if (loginUiState is UiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(18.dp),
                                color = SilverMist,
                                strokeWidth = 1.dp
                            )
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Login,
                                    contentDescription = "Login",
                                    modifier = Modifier.size(21.dp),
                                    tint = if (viewModel.isFormValid)
                                        WhiteFont
                                    else
                                        MaterialTheme.colorScheme.onBackground.copy(0.3f)
                                )
                                Text(
                                    text = "Login",
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = robotoSerifFamily
                                )
                            }
                        }
                    }
                )
            }
        }
    )
}

@Composable
private fun getLoginFields(
    viewModel: LoginViewModel
): List<LoginField> {
    return listOf(
        LoginField(
            name = FormFieldName.Login.EMAIL,
            label = "Email Address",
            value = viewModel.email,
            updateState = {
                viewModel.updateField(
                    FormFieldName.Login.EMAIL, it
                )
            },
            keyboardType = KeyboardType.Email,
            errorMessage = viewModel.emailError
        ),
        LoginField(
            name = FormFieldName.Login.PASSWORD,
            label = "Password",
            value = viewModel.password,
            updateState = {
                viewModel.updateField(
                    FormFieldName.Login.PASSWORD, it
                )
            },
            errorMessage = viewModel.passwordError,
            keyboardType = KeyboardType.Password,
        )
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenPreview() {
    FitnesswayTheme {
        LoginScreen(onBackClick = {})
    }
}