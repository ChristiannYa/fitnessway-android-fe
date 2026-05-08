package com.example.fitnessway.di.modules

import com.example.fitnessway.util.date_time.AppDateTimeFormatter
import com.example.fitnessway.util.date_time.IAppDateTimeFormatter
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun loadAppDateTimeFormatterModule() = module {
    single<IAppDateTimeFormatter> {
        AppDateTimeFormatter(
            timezoneStateHolder = get(),
            scope = get(named("appScope"))
        )
    }
}