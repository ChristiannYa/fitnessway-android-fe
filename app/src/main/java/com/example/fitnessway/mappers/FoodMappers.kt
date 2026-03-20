package com.example.fitnessway.mappers

import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.data.model.m_26.FoodBase
import com.example.fitnessway.data.model.m_26.NutrientIdWithAmount
import com.example.fitnessway.data.model.m_26.PendingFoodAddRequest
import com.example.fitnessway.util.asEnum
import com.example.fitnessway.util.form.FormStates.FoodCreation

fun FoodCreation.toPendingRequest(nutrients: List<NutrientIdWithAmount>) = PendingFoodAddRequest(
    base = FoodBase(
        name = this.name,
        brand = this.brand,
        amountPerServing = this.amountPerServing.toDoubleOrNull() ?: 0.0,
        servingUnit = this.servingUnit.asEnum()
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