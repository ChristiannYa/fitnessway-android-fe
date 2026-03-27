package com.example.fitnessway.util.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import com.example.fitnessway.util.Formatters.logcat

@Composable
fun NavBackStackEntry.OnPopFromStack(onPop: () -> Unit) {
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            logcat("[FoodSelection lifecycle] $event")
            if (event == Lifecycle.Event.ON_DESTROY) onPop()
        }

        lifecycle.addObserver(observer)

        onDispose { lifecycle.removeObserver(observer) }
    }
}