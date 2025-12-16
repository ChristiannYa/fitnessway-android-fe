package com.example.fitnessway.feature.profile.manager

import com.example.fitnessway.feature.profile.manager.colors.ColorsManager
import com.example.fitnessway.feature.profile.manager.goals.GoalsManager

interface IProfileManagers {
    val goals: GoalsManager
    val colors: ColorsManager
}