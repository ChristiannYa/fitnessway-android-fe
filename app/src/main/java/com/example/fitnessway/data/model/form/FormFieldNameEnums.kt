package com.example.fitnessway.data.model.form

import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences

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

    enum class FoodLogEdition : FormFieldName {
        SERVINGS,
        AMOUNT_PER_SERVING
    }

    sealed interface IFoodCreation : FormFieldName

    object FoodCreation {
        enum class BaseField : IFoodCreation {
            NAME,
            BRAND,
            AMOUNT_PER_SERVING,
            SERVING_UNIT
        }

        data class NutrientField(val nutrientWithPreferences: NutrientWithPreferences) : IFoodCreation
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

    data class NutrientGoalData(val nutrientData: NutrientWithPreferences) : FormFieldName

    data class NutrientColorUpdate(val nutrientData: NutrientWithPreferences) : FormFieldName
}