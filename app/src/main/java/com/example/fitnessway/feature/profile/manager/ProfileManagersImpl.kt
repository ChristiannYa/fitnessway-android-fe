package com.example.fitnessway.feature.profile.manager

import com.example.fitnessway.feature.profile.manager.goals.GoalsManager

class ProfileManagersImpl(
    override val goals: GoalsManager
) : IProfileManagers