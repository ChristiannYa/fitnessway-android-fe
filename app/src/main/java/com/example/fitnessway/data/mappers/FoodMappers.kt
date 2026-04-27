package com.example.fitnessway.data.mappers

import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.data.model.m_26.EdibleAddRequest
import com.example.fitnessway.data.model.m_26.EdibleBase
import com.example.fitnessway.data.model.m_26.EdibleInformation
import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.FoodLogCategory
import com.example.fitnessway.data.model.m_26.FoodLogsCategorized
import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.data.model.m_26.ListOption
import com.example.fitnessway.data.model.m_26.NutrientIdWithAmount
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.data.model.m_26.PendingFoodAddRequest
import com.example.fitnessway.data.model.m_26.UserEdible
import com.example.fitnessway.util.form.FormStates.FoodCreation
import com.example.fitnessway.util.toEnum

fun ListOption.toClientView() = when (this) {
    ListOption.SUPPLEMENT -> this.name.toPascalSpaced(false)
    ListOption.FOOD -> this.name.toPascalSpaced(false)
    ListOption.PENDING_FOOD -> "Food Request"
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
    base = EdibleBase(
        name = this.name,
        brand = this.brand,
        amountPerServing = this.amountPerServing.toDoubleOrNull() ?: 0.0,
        servingUnit = this.servingUnit.toEnum()
    ),
    nutrients = nutrients
)

fun FoodCreation.toUserEdibleRequest(
    nutrients: List<NutrientIdWithAmount>,
    edibleType: String
) = EdibleAddRequest(
    base = EdibleBase(
        name = this.name,
        brand = this.brand,
        amountPerServing = this.amountPerServing.toDoubleOrNull() ?: 0.0,
        servingUnit = this.servingUnit.toEnum()
    ),
    nutrients = nutrients,
    edibleType = edibleType
)

fun PendingFood.toPreview() = FoodPreview(
    id = this.id,
    base = this.information.base,
    nutrientPreview = this.information.nutrients.toNutrientPreview(),
    source = EdibleSource.APP
)

fun PendingFood.toFoodInformation() = EdibleInformation(
    base = this.information.base,
    nutrients = this.information.nutrients
)

fun UserEdible.toPreview() = FoodPreview(
    id = this.id,
    base = this.information.base,
    nutrientPreview = this.information.nutrients.toNutrientPreview(),
    source = EdibleSource.USER
)

fun MFood.Model.FoodBaseInfo.toM26FoodBase() = EdibleBase(
    name = this.name,
    brand = this.brand,
    amountPerServing = this.amountPerServing,
    servingUnit = this.servingUnit.toEnum()
)

fun MFood.Model.FoodInformation.toM26FoodInformation() = EdibleInformation(
    base = this.information.toM26FoodBase(),
    nutrients = this.nutrients.toM26NutrientsInFood()
)

fun MFood.Model.FoodInformation.toMFoodPreview() = FoodPreview(
    id = this.information.id,
    base = this.information.toM26FoodBase(),
    nutrientPreview = this.nutrients.toNutrientPreview(),
    source = EdibleSource.USER
)