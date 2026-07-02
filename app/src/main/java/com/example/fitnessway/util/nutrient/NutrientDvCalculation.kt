package com.example.fitnessway.util.nutrient

import com.example.fitnessway.constants.NutrientId

enum class NutrientDvOperation {
    DV_TO_AMOUNT,
    AMOUNT_TO_DV
}

fun getNutrientDv(
    id: Int,
    amount: Double,
    operation: NutrientDvOperation = NutrientDvOperation.DV_TO_AMOUNT
): Double {
    fun dv(standardDv: Double): Double {
        return when (operation) {
            NutrientDvOperation.DV_TO_AMOUNT -> (amount / 100.0) * standardDv
            NutrientDvOperation.AMOUNT_TO_DV -> (amount / standardDv) * 100.0
        }
    }

    return when (id) {
        NutrientId.Base.CARBS -> dv(275.0)
        NutrientId.Base.CHOL -> dv(300.0)
        NutrientId.Base.FIBER -> dv(28.0)
        NutrientId.Base.PROTEIN -> dv(50.0)
        NutrientId.Base.SODIUM -> dv(2300.0)
        NutrientId.Base.SATURATED_FAT -> dv(20.0)
        NutrientId.Base.ADDED_SUGAR -> dv(50.0)

        NutrientId.Vitamins.A -> dv(900.0)
        NutrientId.Vitamins.THIAMIN -> dv(1.2)
        NutrientId.Vitamins.NIACIN -> dv(16.0)
        NutrientId.Vitamins.PANTOTHENIC_ACID -> dv(5.0)
        NutrientId.Vitamins.B6 -> dv(1.7)
        NutrientId.Vitamins.BIOTIN -> dv(30.0)
        NutrientId.Vitamins.FOLATE -> dv(400.0)
        NutrientId.Vitamins.B12 -> dv(2.4)
        NutrientId.Vitamins.C -> dv(90.0)
        NutrientId.Vitamins.D -> dv(20.0)
        NutrientId.Vitamins.E -> dv(15.0)
        NutrientId.Vitamins.K -> dv(120.0)

        NutrientId.Minerals.CALCIUM -> dv(1300.0)
        NutrientId.Minerals.IRON -> dv(18.0)
        NutrientId.Minerals.MAGNESIUM -> dv(420.0)
        NutrientId.Minerals.POTASSIUM -> dv(4700.0)
        NutrientId.Minerals.ZINC -> dv(11.0)
        NutrientId.Minerals.SELENIUM -> dv(55.0)
        NutrientId.Minerals.PHOSPHORUS -> dv(1250.0)
        NutrientId.Minerals.MANGANESE -> dv(2.3)
        NutrientId.Minerals.IODINE -> dv(150.0)
        NutrientId.Minerals.COPPER -> dv(0.9)

        else -> throw IllegalArgumentException(
            "Nutrient with id=${id} does not support %DV conversion"
        )
    }
}