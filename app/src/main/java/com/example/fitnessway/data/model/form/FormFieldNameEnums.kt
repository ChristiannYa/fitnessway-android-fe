package com.example.fitnessway.data.model.form

import com.example.fitnessway.data.model.nutrient.Nutrient

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

    sealed interface IFoodCreation : FormFieldName

    object FoodCreation {
        enum class BaseField : IFoodCreation {
            NAME,
            BRAND,
            AMOUNT_PER_SERVING,
            SERVING_UNIT
        }

        data class NutrientField(val nutrient: Nutrient) : IFoodCreation
    }

    sealed interface IFoodEdition : FormFieldName

    object FoodEdition {
        enum class DetailField : IFoodEdition {
            NAME,
            BRAND,
            AMOUNT_PER_SERVING,
            SERVING_UNIT
        }

        data class NutrientField(val nutrient: Nutrient) : IFoodEdition
    }
}