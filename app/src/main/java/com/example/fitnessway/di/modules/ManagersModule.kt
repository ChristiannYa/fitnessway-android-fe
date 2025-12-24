package com.example.fitnessway.di.modules

import com.example.fitnessway.feature.home.manager.date.DateManager
import com.example.fitnessway.feature.home.manager.foodlog.FoodLogManager
import com.example.fitnessway.feature.home.manager.HomeManagersImpl
import com.example.fitnessway.feature.home.manager.IHomeManagers
import com.example.fitnessway.feature.lists.manager.food.FoodManager
import com.example.fitnessway.feature.home.manager.ui.UiManager
import com.example.fitnessway.feature.lists.manager.IListsManagers
import com.example.fitnessway.feature.lists.manager.ListsManagersImpl
import com.example.fitnessway.feature.lists.manager.edition.EditionManager
import com.example.fitnessway.feature.lists.manager.toggle.SelectionManager
import com.example.fitnessway.feature.profile.manager.IProfileManagers
import com.example.fitnessway.feature.profile.manager.ProfileManagersImpl
import com.example.fitnessway.feature.profile.manager.colors.ColorsManager
import com.example.fitnessway.feature.profile.manager.goals.GoalsManager
import org.koin.dsl.module

val managersModule = module {
    single<IHomeManagers> {
        HomeManagersImpl(
            date = DateManager(),
            foodLog = FoodLogManager(),
            ui = UiManager()
        )
    }

    single<IListsManagers> {
        ListsManagersImpl(
            selection = SelectionManager(),
            edition = EditionManager(),
            food = FoodManager()
        )
    }

    single<IProfileManagers> {
        ProfileManagersImpl(
            goals = GoalsManager(),
            colors = ColorsManager()
        )
    }
}