package com.example.fitnessway.util.form.field

import com.example.fitnessway.util.form.field.rules.NameInlineRules
import com.example.fitnessway.util.form.field.rules.PasswordInlineRules

object Rules {
    val passwordRules = listOf(
        PasswordInlineRules::longerThanRule,
        PasswordInlineRules::shorterThanMaxRule,
        PasswordInlineRules::withoutWhitespaceRule,
        PasswordInlineRules::hasDigitRule,
        PasswordInlineRules::hasUppercaseRule,
        PasswordInlineRules::hasSpecialCharRule
    )

    val nameRules = listOf(
        NameInlineRules::notEmptyRule,
        NameInlineRules::lengthRule,
        NameInlineRules::validCharactersRule,
        NameInlineRules::validFormatRule
    )
}