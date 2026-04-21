package com.example.fitnessway.data.mappers

import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.data.model.m_26.FoodBase
import com.example.fitnessway.data.model.m_26.FoodInformation
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.FoodLogCategory
import com.example.fitnessway.data.model.m_26.FoodLogsCategorized
import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.data.model.m_26.FoodSource
import com.example.fitnessway.data.model.m_26.ListOption
import com.example.fitnessway.data.model.m_26.NutrientIdWithAmount
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.data.model.m_26.PendingFoodAddRequest
import com.example.fitnessway.data.model.m_26.UserEdible
import com.example.fitnessway.util.form.FormStates.FoodCreation
import com.example.fitnessway.util.toEnum

fun ListOption.toClientView() = when (this) {
    ListOption.Supplement -> this.name.toPascalSpaced(false)
    ListOption.Food -> this.name.toPascalSpaced(false)
    ListOption.PendingFood -> "Food Request"
}

fun FoodLogsCategorized.mapfl(
    transform: (
        category: FoodLogCategory,
        logs: List<FoodLog>
    ) -> List<FoodLog>
): FoodLogsCategorized = FoodLogsCategorized(
    breakfast = transform(FoodLogCategory.BREAKFAST, breakfast),
    lunch = transform(FoodLogCategory.LUNCH, lunch),
    dinner = transform(FoodLogCategory.DINNER, dinner),
    supplement = transform(FoodLogCategory.SUPPLEMENT, supplement)
)

fun FoodCreation.toPendingRequest(nutrients: List<NutrientIdWithAmount>) = PendingFoodAddRequest(
    base = FoodBase(
        name = this.name,
        brand = this.brand,
        amountPerServing = this.amountPerServing.toDoubleOrNull() ?: 0.0,
        servingUnit = this.servingUnit.toEnum()
    ),
    nutrients = nutrients
)

fun FoodCreation.toUserFoodRequest(nutrients: List<NutrientIdWithAmount>) = MFood.Api.Req.FoodAddRequest(
    information = MFood.Model.FoodBaseInfo(
        id = 0,
        name = this.name,
        brand = this.brand,
        amountPerServing = this.amountPerServing.toDoubleOrNull() ?: 0.0,
        servingUnit = this.servingUnit
    ),
    nutrients = nutrients
)

fun PendingFood.toPreview() = FoodPreview(
    id = this.id,
    base = this.information.base,
    nutrientPreview = this.information.nutrients.toNutrientPreview(),
    source = FoodSource.APP
)

fun PendingFood.toFoodInformation() = FoodInformation(
    base = this.information.base,
    nutrients = this.information.nutrients
)

fun UserEdible.toPreview() = FoodPreview(
    id = this.id,
    base = this.information.base,
    nutrientPreview = this.information.nutrients.toNutrientPreview(),
    source = FoodSource.USER
)

fun MFood.Model.FoodBaseInfo.toM26FoodBase() = FoodBase(
    name = this.name,
    brand = this.brand,
    amountPerServing = this.amountPerServing,
    servingUnit = this.servingUnit.toEnum()
)

fun MFood.Model.FoodInformation.toM26FoodInformation() = FoodInformation(
    base = this.information.toM26FoodBase(),
    nutrients = this.nutrients.toM26NutrientsInFood()
)

fun MFood.Model.FoodInformation.toMFoodPreview() = FoodPreview(
    id = this.information.id,
    base = this.information.toM26FoodBase(),
    nutrientPreview = this.nutrients.toNutrientPreview(),
    source = FoodSource.USER
)