package com.example.fitnessway.data.model.welcome

object NameConfig {
   const val MIN_LENGTH = 2
   const val MAX_LENGTH = 24
   const val SPECIAL_CHARACTERS = " '-."

   const val ERR_LEN = "Name must be between $MIN_LENGTH and $MAX_LENGTH characters!"
   const val ERR_EMPTY = "Name cannot be empty!"
   const val ERR_INVALID_CHARS = "Name can only contain letters, spaces, hyphens, apostrophes, and periods!"
   const val ERR_INVALID_FORMAT = "Name cannot start or end with special characters!"
}