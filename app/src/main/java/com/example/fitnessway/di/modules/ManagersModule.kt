package com.example.fitnessway.di.modules

import com.example.fitnessway.feature.home.manager.HomeManagerImpl
import com.example.fitnessway.feature.home.manager.IHomeManager
import com.example.fitnessway.feature.home.manager.date.DateManager
import com.example.fitnessway.feature.home.manager.foodlog.FoodLogManager
import com.example.fitnessway.feature.home.manager.ui.UiManager
import com.example.fitnessway.feature.lists.manager.IListsManagers
import com.example.fitnessway.feature.lists.manager.ListsManagersImpl
import com.example.fitnessway.feature.lists.manager.creation.CreationManager
import com.example.fitnessway.feature.lists.manager.edition.EditionManager
import com.example.fitnessway.feature.lists.manager.request.FoodRequestManager
import com.example.fitnessway.feature.profile.manager.IProfileManagers
import com.example.fitnessway.feature.profile.manager.ProfileManagersImpl
import com.example.fitnessway.feature.profile.manager.colors.ColorsManager
import com.example.fitnessway.feature.profile.manager.goals.GoalsManager
import org.koin.dsl.module
import java.time.ZoneId

fun loadManagerModules(timezone: ZoneId) = module {
    single<IHomeManager> {
        HomeManagerImpl(
            date = DateManager(timezone),
            foodLog = FoodLogManager(),
            ui = UiManager()
        )
    }

    single<IListsManagers> {
        ListsManagersImpl(
            edition = EditionManager(),
            creation = CreationManager(),
            request = FoodRequestManager()
        )
    }

    single<IProfileManagers> {
        ProfileManagersImpl(
            goals = GoalsManager(),
            colors = ColorsManager()
        )
    }
}