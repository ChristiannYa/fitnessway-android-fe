package com.example.fitnessway.util

import android.util.Log
import com.example.fitnessway.util.Constants.LogLevel

fun logcat(
    message: String,
    level: LogLevel = LogLevel.DEBUG
) {
    when (level) {
        LogLevel.DEBUG -> Log.d(Constants.DEBUG_TAG, message)
        LogLevel.ERROR -> Log.e(Constants.DEBUG_TAG, message)
        LogLevel.INFO -> Log.i(Constants.DEBUG_TAG, message)
        LogLevel.WARN -> Log.w(Constants.DEBUG_TAG, message)
        LogLevel.VERBOSE -> Log.v(Constants.DEBUG_TAG, message)
    }
}