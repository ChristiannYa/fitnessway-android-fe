package com.example.fitnessway.util.extensions

import com.example.fitnessway.data.model.MFood

fun List<MFood.Model.FoodInformation>.findById(id: Int) = find { it.information.id == id }