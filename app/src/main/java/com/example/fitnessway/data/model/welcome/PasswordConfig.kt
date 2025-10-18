package com.example.fitnessway.data.model.welcome

object PasswordConfig {
   const val MIN_LENGTH = 8
   const val MAX_LENGTH = 128
   const val MIN_DIGITS = 1
   const val MIN_UPPERCASE = 1
   const val MIN_SPECIAL = 1
   const val SPECIAL_CHARACTERS = "_%-=+#@!$&*"

   const val ERR_LEN = "Password must be between $MIN_LENGTH and $MAX_LENGTH characters"
   const val ERR_WHITESPACE = "Password must not contain whitespace"
   const val ERR_DIGIT = "Password must contain at least $MIN_DIGITS digit(s)"
   const val ERR_UPPER = "Password must have at least $MIN_UPPERCASE uppercase letter(s)"
   const val ERR_SPECIAL = "Password must have at least $MIN_SPECIAL special character(s) from: $SPECIAL_CHARACTERS"
}