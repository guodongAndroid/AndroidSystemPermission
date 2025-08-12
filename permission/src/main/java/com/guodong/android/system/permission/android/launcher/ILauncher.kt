package com.guodong.android.system.permission.android.launcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import rikka.hidden.compat.LauncherApis

/**
 * Created by john.wick on 2025/5/27
 */
internal interface ILauncher {

    companion object {
        private const val TAG = "ILauncher"
    }

    fun getLauncher(context: Context): ComponentName? {
        return LauncherApis.getLauncherNoThrow()
    }

    fun setLauncher(context: Context, packageName: String): Boolean {
        return LauncherApis.setLauncherNoThrow(context, packageName)
    }

    fun openSystemLauncher(context: Context) {
        try {
            context.startActivity(Intent().apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                component = ComponentName("com.android.launcher3", "com.android.launcher3.Launcher")
            })
        } catch (ignore: Exception) {
            try {
                context.startActivity(Intent().apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    component = ComponentName(
                        "com.android.launcher3",
                        "com.android.launcher3.uioverrides.QuickstepLauncher"
                    )
                })
            } catch (e: Exception) {
                Log.e(TAG, "openSystemLauncher: ${e.message}", e)
            }
        }
    }
}