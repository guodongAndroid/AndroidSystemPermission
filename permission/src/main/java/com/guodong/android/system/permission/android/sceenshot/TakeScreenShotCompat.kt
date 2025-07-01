package com.guodong.android.system.permission.android.sceenshot

import android.content.Context
import android.graphics.Bitmap
import android.os.Build

/**
 * Created by john.wick on 2025/6/30
 */
internal object TakeScreenShotCompat : ITakeScreenShot {

    private val capture = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            TakeScreenShotApi34
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            TakeScreenShotApi31
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
            TakeScreenShotApi28
        }

        else -> {
            TakeScreenShotApi24
        }
    }

    override suspend fun takeScreenShot(context: Context, savePath: String): Boolean {
        return capture.takeScreenShot(context, savePath)
    }

    override fun takeScreenShot(context: Context): Bitmap? {
        return capture.takeScreenShot(context)
    }

}