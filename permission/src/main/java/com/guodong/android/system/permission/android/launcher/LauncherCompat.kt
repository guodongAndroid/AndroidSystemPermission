package com.guodong.android.system.permission.android.launcher

import android.content.ComponentName
import android.content.Context
import android.os.Build

/**
 * Created by john.wick on 2025/4/15
 */
internal object LauncherCompat : ILauncher {

    override fun getLauncher(context: Context): ComponentName? {
        return super.getLauncher(context)
    }

    override fun setLauncher(context: Context, packageName: String): Boolean {
        return super.setLauncher(context, packageName)
    }

    override fun openSystemLauncher(context: Context) {
        super.openSystemLauncher(context)
    }
}