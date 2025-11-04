package com.example.fitnessway.util.form.field.rules

import kotlin.reflect.KFunction1
import com.example.fitnessway.util.form.field.Configuration.Registration.Name as NameConfig

@JvmInline
value class NameInlineRules(val value: String) {
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

    infix fun checkWith(rules: List<KFunction1<NameInlineRules, Unit>>) = runCatching {
        rules.forEach { it(this) }
    }
}