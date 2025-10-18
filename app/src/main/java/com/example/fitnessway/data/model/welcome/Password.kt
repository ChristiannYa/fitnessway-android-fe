package com.example.fitnessway.data.model.welcome

import kotlin.reflect.KFunction1

@JvmInline
value class Password(val pwd: String) {
   fun longerThanRule() = require(
      pwd.length >= PasswordConfig.MIN_LENGTH
   ) { PasswordConfig.ERR_LEN }

   fun shorterThanMaxRule() = require(
      pwd.length <= PasswordConfig.MAX_LENGTH
   ) { PasswordConfig.ERR_LEN }

   fun withoutWhitespaceRule() = require(
      pwd.none { it.isWhitespace() }
   ) { PasswordConfig.ERR_WHITESPACE }

   fun hasDigitRule() = require(
      pwd.count { it.isDigit() } >= PasswordConfig.MIN_DIGITS
   ) { PasswordConfig.ERR_DIGIT }

   fun hasUppercaseRule() = require(
      pwd.count { it.isUpperCase() } >= PasswordConfig.MIN_UPPERCASE
   ) { PasswordConfig.ERR_UPPER }

   fun hasSpecialCharRule() = require(
      pwd.count { it in PasswordConfig.SPECIAL_CHARACTERS } >= PasswordConfig.MIN_SPECIAL
   ) { PasswordConfig.ERR_SPECIAL }

   infix fun checkWith(rules: List<KFunction1<Password, Unit>>) = runCatching {
      rules.forEach { it(this) }
   }

   infix fun validateWith(rules: List<KFunction1<Password, Unit>>) = runCatching {
      val message = rules.mapNotNull {
         runCatching { it(this) }.exceptionOrNull()?.message
      }.joinToString(separator = "\n")

      require(message.isEmpty()) { message }
   }
}