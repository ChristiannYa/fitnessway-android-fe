package com.example.fitnessway.util.extensions

import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.data.model.m_26.FoodBase
import com.example.fitnessway.data.model.m_26.ServingUnit

fun List<MFood.Model.FoodInformation>.findById(id: Int) = find { it.information.id == id }

fun FoodBase.calcAmountPerServing() = if (this.servingUnit == ServingUnit.OZ) {
    this.amountPerServing * 28.3495
} else this.amountPerServing