package com.example.fitnessway.util

import com.example.fitnessway.util.Formatters.logcat

object Debug {
    fun Boolean.logCheckOrCross(valName: String) {
        logcat("  [${if (this) "✅" else "❌"}] $valName")
    }

    fun Map<Int, Any>.logMap() {
        this.forEach {
            logcat("    [${it.key}]: ${it.value}")
        }
    }

    fun List<Pair<Int, Any>>.logPairs() {
        this.forEach { it.logPair() }
    }

    fun Pair<Int, Any>.logPair() {
        logcat("    [${this.first}]: ${this.second}")
    }
}