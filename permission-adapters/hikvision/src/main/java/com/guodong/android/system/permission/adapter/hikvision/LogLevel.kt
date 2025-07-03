package com.guodong.android.system.permission.adapter.hikvision

import androidx.annotation.StringDef

/**
 * Created by john.wick on 2025/7/3
 */
@StringDef(
    LogLevel.ASSERT,
    LogLevel.ERROR,
    LogLevel.WARN,
    LogLevel.INFO,
    LogLevel.DEBUG,
    LogLevel.VERBOSE,
)
internal annotation class LogLevel {
    companion object {
        const val ASSERT = "A"
        const val ERROR = "E"
        const val WARN = "W"
        const val INFO = "I"
        const val DEBUG = "D"
        const val VERBOSE = "V"
    }
}
