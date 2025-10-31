package com.example.fitnessway.feature.home.manager

class HomeManagersImpl (
    override val date: DateManager,
    override val foodLog: FoodLogManager
) : IHomeManagers