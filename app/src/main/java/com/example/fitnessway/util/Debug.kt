package com.example.fitnessway.util

import com.example.fitnessway.util.Formatters.logcat

object Debug {
    fun Boolean.logCheckOrCross(valName: String) {
        logcat("  [${if (this) "✅" else "❌"}] $valName")
    }
}