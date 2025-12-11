package com.example.fitnessway.di.modules

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val scopeModule = module {
    single(named("repositoryScope")) {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}