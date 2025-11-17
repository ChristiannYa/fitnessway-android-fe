package com.example.fitnessway.util.form.field

import com.example.fitnessway.util.form.field.InlineRules.AuthForm as AF
import com.example.fitnessway.util.form.field.InlineRules.FoodCreation as FC

object Rules {
    object AuthForm {
        val passwordRules = listOf(
            AF.PasswordInlineRules::longerThanRule,
            AF.PasswordInlineRules::shorterThanMaxRule,
            AF.PasswordInlineRules::withoutWhitespaceRule,
            AF.PasswordInlineRules::hasDigitRule,
            AF.PasswordInlineRules::hasUppercaseRule,
            AF.PasswordInlineRules::hasSpecialCharRule
        )
        val nameRules = listOf(
            AF.NameInlineRules::notEmptyRule,
            AF.NameInlineRules::lengthRule,
            AF.NameInlineRules::validCharactersRule,
            AF.NameInlineRules::validFormatRule
        )
    }

    object FoodCreation {
        val nameRules = listOf(
            FC.NameInlineRules::lengthRule,
            FC.NameInlineRules::notEmptyRule,
            FC.NameInlineRules::validCharactersRule,
            FC.NameInlineRules::validFormatRule
        )
        val brandRules = listOf(
            FC.BrandInlineRules::lengthRule,
            FC.BrandInlineRules::validCharacters,
            FC.BrandInlineRules::validFormatRule
        )
    }
}