package com.example.fitnessway.feature.lists.manager.request

import com.example.fitnessway.data.model.m_26.FoodSource
import com.example.fitnessway.util.food.creation.FoodCreation

class FoodRequestManager : IFoodRequestManager, FoodCreation(FoodSource.APP)