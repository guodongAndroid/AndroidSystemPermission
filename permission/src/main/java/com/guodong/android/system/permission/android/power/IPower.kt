package com.guodong.android.system.permission.android.power

import android.os.SystemClock
import rikka.hidden.compat.PowerManagerApis

/**
 * Created by john.wick on 2025/6/30
 */
internal interface IPower {

    fun reboot() {
        PowerManagerApis.rebootNoThrow("")
    }

    fun shutdown() {
        PowerManagerApis.shutdownNoThrow("")
    }

    fun goToSleep() {
        PowerManagerApis.goToSleepNoThrow(SystemClock.uptimeMillis())
    }

    fun wakeUp() {
        PowerManagerApis.wakeUpNoThrow(SystemClock.uptimeMillis())
    }
}