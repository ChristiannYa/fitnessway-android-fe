package com.example.fitnessway.data.model.form

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

    enum class FoodLog : FormFieldName {
        SERVINGS,
        AMOUNT_PER_SERVING,
        TIME
    }
}