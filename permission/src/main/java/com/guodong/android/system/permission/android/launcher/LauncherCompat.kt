package com.guodong.android.system.permission.android.launcher

import android.content.ComponentName
import android.content.Context
import android.os.Build

/**
 * Created by john.wick on 2025/4/15
 */
internal object LauncherCompat : ILauncher {

    private val launcher = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            LauncherApi29
        }

        else -> {
            LauncherApi24
        }
    }

    override fun getLauncher(context: Context): ComponentName? {
        return launcher.getLauncher(context)
    }

    override fun setLauncher(context: Context, packageName: String): Boolean {
        return launcher.setLauncher(context, packageName)
    }

    override fun openSystemLauncher(context: Context) {
        launcher.openSystemLauncher(context)
    }
}