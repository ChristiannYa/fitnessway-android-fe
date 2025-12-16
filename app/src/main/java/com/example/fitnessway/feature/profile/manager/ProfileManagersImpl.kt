package com.example.fitnessway.feature.profile.manager

import com.example.fitnessway.feature.profile.manager.colors.ColorsManager
import com.example.fitnessway.feature.profile.manager.goals.GoalsManager

class ProfileManagersImpl(
    override val goals: GoalsManager,
    override val colors: ColorsManager
) : IProfileManagers