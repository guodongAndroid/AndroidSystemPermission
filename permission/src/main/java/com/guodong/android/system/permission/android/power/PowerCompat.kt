package com.guodong.android.system.permission.android.power

import android.content.Context
import android.os.Build

/**
 * Created by john.wick on 2025/6/30
 */
internal object PowerCompat : IPower {

    private val power = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            PowerApi29
        }

        else -> {
            PowerApi25
        }
    }

    override fun goToSleep(context: Context): Boolean {
        return power.goToSleep(context)
    }

    override fun wakeUp(context: Context): Boolean {
        return power.wakeUp(context)
    }
}