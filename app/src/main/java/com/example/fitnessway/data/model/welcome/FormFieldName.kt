package com.example.fitnessway.data.model.welcome

sealed interface FormFieldName {
   enum class Login : FormFieldName {
      EMAIL,
      PASSWORD
   }

   enum class Register : FormFieldName {
      NAME,
      EMAIL,
      PASSWORD,
      CONFIRM_PASSWORD,
   }
}