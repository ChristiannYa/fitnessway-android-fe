package com.example.fitnessway.data.model.welcome

import kotlin.reflect.KFunction1

@JvmInline
value class Name(val value: String) {
   fun lengthRule() = require(
      value.length in NameConfig.MIN_LENGTH..NameConfig.MAX_LENGTH
   ) { NameConfig.ERR_LEN }

   fun notEmptyRule() = require(
      value.isNotBlank()
   ) { NameConfig.ERR_EMPTY }

   fun validCharactersRule() = require(
      value.all { it.isLetter() || it in NameConfig.SPECIAL_CHARACTERS }
   ) { NameConfig.ERR_INVALID_CHARS }

   fun validFormatRule() = require(
      value.first().isLetter() && value.last().isLetter()
   ) { NameConfig.ERR_INVALID_FORMAT }

   infix fun checkWith(rules: List<KFunction1<Name, Unit>>) = runCatching {
      rules.forEach { it(this) }
   }
}
