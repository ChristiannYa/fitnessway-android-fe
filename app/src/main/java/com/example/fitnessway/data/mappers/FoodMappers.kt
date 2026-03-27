package com.example.fitnessway.data.mappers

import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.data.model.m_26.FoodBase
import com.example.fitnessway.data.model.m_26.FoodInformation
import com.example.fitnessway.data.model.m_26.NutrientIdWithAmount
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.data.model.m_26.PendingFoodAddRequest
import com.example.fitnessway.util.form.FormStates.FoodCreation
import com.example.fitnessway.util.toEnum

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

fun PendingFood.toM25FoodInformation() = MFood.Model.FoodInformation(
    information = MFood.Model.FoodBaseInfo(
        id = this.id,
        name = this.information.base.name,
        brand = this.information.base.brand,
        amountPerServing = this.information.base.amountPerServing,
        servingUnit = this.information.base.servingUnit.name
    ),
    metadata = MFood.Model.FoodMetaData(
        isFavorite = false,
        lastLoggedAt = null,
        createdAt = this.createdAt.toString(),
        updatedAt = ""
    ),
    nutrients = this.information.nutrients.toM25NutrientsInFood()
)