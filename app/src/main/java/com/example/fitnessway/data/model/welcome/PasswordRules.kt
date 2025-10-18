package com.example.fitnessway.data.model.welcome

val passwordRules = listOf(
   Password::longerThanRule,
   Password::shorterThanMaxRule,
   Password::withoutWhitespaceRule,
   Password::hasDigitRule,
   Password::hasUppercaseRule,
   Password::hasSpecialCharRule
)