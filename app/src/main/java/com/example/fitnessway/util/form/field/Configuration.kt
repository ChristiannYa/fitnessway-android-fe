package com.example.fitnessway.util.form.field

object Configuration {
    object Registration {
        object Name {
            const val MIN_LENGTH = 2
            const val MAX_LENGTH = 24
            const val SPECIAL_CHARACTERS = " '-."

            const val ERR_LEN = "Name must be between $MIN_LENGTH and $MAX_LENGTH characters!"
            const val ERR_EMPTY = "Name cannot be empty!"
            const val ERR_INVALID_CHARS =
                "Name can only contain letters, spaces, hyphens, apostrophes, and periods!"
            const val ERR_INVALID_FORMAT = "Name cannot start or end with special characters!"
        }

        object Password {
            const val MIN_LENGTH = 8
            const val MAX_LENGTH = 64
            const val MIN_DIGITS = 1
            const val MIN_UPPERCASE = 1
            const val MIN_SPECIAL = 1
            const val SPECIAL_CHARACTERS = "_%-=+#@!$&*"

            const val ERR_LEN = "Password must be between $MIN_LENGTH and $MAX_LENGTH characters"
            const val ERR_WHITESPACE = "Password must not contain whitespace"
            const val ERR_DIGIT = "Password must contain at least $MIN_DIGITS digit(s)"
            const val ERR_UPPER = "Password must have at least $MIN_UPPERCASE uppercase letter(s)"
            const val ERR_SPECIAL =
                "Password must have at least $MIN_SPECIAL special character(s) from: $SPECIAL_CHARACTERS"
        }
    }
}