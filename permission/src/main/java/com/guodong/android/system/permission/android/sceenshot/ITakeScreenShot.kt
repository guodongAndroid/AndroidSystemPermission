package com.guodong.android.system.permission.android.sceenshot

import android.content.Context
import android.graphics.Bitmap

/**
 * Created by john.wick on 2025/6/30
 */
internal interface ITakeScreenShot {

    /**
     * 屏幕截图
     */
    suspend fun takeScreenShot(context: Context, savePath: String): Boolean

    /**
     * 屏幕截图
     */
    fun takeScreenShot(context: Context): Bitmap?
}