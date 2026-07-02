package com.example.fitnessway.constants

object NutrientId {
    object Base {
        const val CALS = 1
        const val CARBS = 2
        const val CHOL = 3
        const val FATS = 4
        const val FIBER = 5
        const val PROTEIN = 6
        const val SODIUM = 7
        const val SUGAR = 8
        const val SATURATED_FAT = 17
        const val TRANS_FAT = 18
        const val POLYUNSATURATED_FAT = 19
        const val MONOUNSATURATED_FAT = 20
        const val OMEGA_3 = 21
        const val ADDED_SUGAR = 22
        const val DIETARY_FIBER = 23
        val ALL = setOf(
            CALS,
            CARBS,
            CHOL,
            FATS,
            FIBER,
            PROTEIN,
            SODIUM,
            SUGAR,
            SATURATED_FAT,
            TRANS_FAT,
            POLYUNSATURATED_FAT,
            MONOUNSATURATED_FAT,
            OMEGA_3,
            ADDED_SUGAR,
            DIETARY_FIBER
        )
    }

    object Vitamins {
        const val A = 9
        const val B12 = 10
        const val C = 11
        const val D = 12
        const val THIAMIN = 24
        const val NIACIN = 25
        const val PANTOTHENIC_ACID = 26
        const val B6 = 27
        const val BIOTIN = 28
        const val FOLATE = 29
        const val E = 30
        const val K = 31
    }

    object Minerals {
        const val CALCIUM = 13
        const val IRON = 14
        const val MAGNESIUM = 15
        const val POTASSIUM = 16
        const val ZINC = 32
        const val SELENIUM = 33
        const val PHOSPHORUS = 34
        const val MANGANESE = 35
        const val IODINE = 36
        const val COPPER = 37
    }
}
