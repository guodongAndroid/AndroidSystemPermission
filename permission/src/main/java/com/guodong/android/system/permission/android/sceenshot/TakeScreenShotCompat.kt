package com.guodong.android.system.permission.android.sceenshot

import android.content.Context
import android.graphics.Bitmap
import android.os.Build

/**
 * Created by john.wick on 2025/6/30
 */
internal object TakeScreenShotCompat : ITakeScreenShot {

    override suspend fun takeScreenShot(context: Context, savePath: String): Boolean {
        return super.takeScreenShot(context, savePath)
    }

    override fun takeScreenShot(context: Context): Bitmap? {
        return super.takeScreenShot(context)
    }

}