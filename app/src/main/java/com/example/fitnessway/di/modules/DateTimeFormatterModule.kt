package com.example.fitnessway.di.modules

import com.example.fitnessway.util.date_time.AppDateTimeFormatter
import com.example.fitnessway.util.date_time.IAppDateTimeFormatter
import org.koin.dsl.module
import java.time.ZoneId

fun loadAppDateTimeFormatterModule(timezone: ZoneId) = module {
    single<ZoneId> { timezone }
    single<IAppDateTimeFormatter> { AppDateTimeFormatter(timezone) }
}