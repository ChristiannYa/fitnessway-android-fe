package com.example.fitnessway.util.form.field

import com.example.fitnessway.util.form.field.Configuration.AuthForm.Name as NameConfig
import com.example.fitnessway.util.form.field.Configuration.AuthForm.Password as PasswordConfig
import com.example.fitnessway.util.form.field.Configuration.FoodCreation.Name as FoodNameConfig
import com.example.fitnessway.util.form.field.Configuration.FoodCreation.Brand as FoodBrandConfig
import kotlin.reflect.KFunction1

object InlineRules {
    object AuthForm {
        @JvmInline
        value class PasswordInlineRules(val pwd: String) {
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

            infix fun checkWith(rules: List<KFunction1<PasswordInlineRules, Unit>>) = runCatching {
                rules.forEach { it(this) }
            }

            infix fun validateWith(rules: List<KFunction1<PasswordInlineRules, Unit>>) = runCatching {
                val message = rules.mapNotNull {
                    runCatching { it(this) }.exceptionOrNull()?.message
                }.joinToString(separator = "\n")

                require(message.isEmpty()) { message }
            }
        }

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
    }

    object FoodCreation {
        @JvmInline
        value class NameInlineRules(val value: String) {
            fun lengthRule() = require(
                value.length in FoodNameConfig.MIN_LENGTH..FoodNameConfig.MAX_LENGTH
            ) { FoodNameConfig.ERR_LEN }

            fun notEmptyRule() = require(
                value.isNotBlank()
            ) { FoodNameConfig.ERR_EMPTY }

            fun validCharactersRule() = require(
                value.all { it.isLetter() || it in FoodNameConfig.SPECIAL_CHARACTERS }
            ) { FoodNameConfig.ERR_INVALID_CHARS }

            fun validFormatRule() = require(
                value.first().isLetter() && value.last().isLetter()
            ) { FoodNameConfig.ERR_INVALID_FORMAT }

            infix fun checkWith(rules: List<KFunction1<NameInlineRules, Unit>>) = runCatching {
                rules.forEach { it(this) }
            }
        }

        @JvmInline
        value class BrandInlineRules(val value: String) {
            fun lengthRule() = require(
                value.length in FoodBrandConfig.MIN_LENGTH..FoodBrandConfig.MAX_LENGTH
            ) { FoodBrandConfig.ERR_LEN }

            fun validCharacters() = require(
                value.all { it.isLetter() || it in FoodBrandConfig.SPECIAL_CHARACTERS }
            ) { FoodBrandConfig.ERR_INVALID_CHARS }

            fun validFormatRule() = require(
                value.first().isLetter() && value.last().isLetter()
            ) { FoodBrandConfig.ERR_INVALID_FORMAT }

            infix fun checkWith(rules: List<KFunction1<BrandInlineRules, Unit>>) = runCatching {
                rules.forEach { it(this) }
            }
        }
    }
}