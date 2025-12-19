package com.example.fitnessway.feature.welcome.screen.register.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.fitnessway.R
import com.example.fitnessway.data.model.form.RegisterField

@Composable
fun RegisterTextField(
   field: RegisterField,
   modifier: Modifier = Modifier,
   isPasswordVisible: Boolean = false,
   onPasswordVisibilityToggle: (() -> Unit)? = null,
) {
   Column(
      verticalArrangement = Arrangement.spacedBy(2.dp)
   ) {
       val isPassword = field.keyboardOptions.keyboardType == KeyboardType.Password

      BasicTextField(
         value = field.value,
         onValueChange = field.updateState,
         textStyle = TextStyle(
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground,
         ),
         modifier = modifier
            .border(
               2.dp,
               if (field.errorMessage != null)
                  MaterialTheme.colorScheme.error
               else
                  MaterialTheme.colorScheme.surfaceVariant,
               shape = RoundedCornerShape(10.dp)
            )
            .padding(14.dp)
            .fillMaxWidth(),
         visualTransformation = if (isPassword && !isPasswordVisible)
            PasswordVisualTransformation()
         else
            VisualTransformation.None,
         keyboardOptions = field.keyboardOptions,
         singleLine = true,
         decorationBox = { innerTextField ->
            Box {
               Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement = Arrangement.spacedBy(6.dp)
               ) {
                  Text(
                     text = field.label,
                     fontFamily = FontFamily.Serif,
                     fontSize = MaterialTheme.typography.labelMedium.fontSize,
                     color = if (field.errorMessage != null)
                        MaterialTheme.colorScheme.error
                     else
                        MaterialTheme.colorScheme.onSurface.copy(0.8f),
                     style = LocalTextStyle.current.merge(
                        TextStyle(
                           lineHeight = 1.em,
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
                  innerTextField()
               }

               if (isPassword) {
                  IconButton(
                     onClick = { onPasswordVisibilityToggle?.invoke() },
                     modifier = Modifier
                        .size(21.dp)
                        .align(Alignment.CenterEnd)
                  ) {
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
                     )
                  }
               }
            }
         },
      )

      // Show error message if present
      field.errorMessage?.let { error ->
         Text(
            text = error,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 6.dp)
         )
      }
   }
}