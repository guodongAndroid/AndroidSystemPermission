package com.guodong.android.system.permission.android.sceenshot

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.hidden.compat.TakeScreenshotApis
import java.io.File

/**
 * Created by john.wick on 2025/6/30
 */
internal interface ITakeScreenShot {

    /**
     * 屏幕截图
     */
    suspend fun takeScreenShot(context: Context, savePath: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(savePath)
        if (file.isDirectory) {
            throw IllegalArgumentException("savePath is directory")
        }

        if (!file.exists()) {
            val parentFile = file.parentFile ?: throw IllegalArgumentException("savePath is no parent path")
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            file.createNewFile()
        }

        val bitmap = takeScreenShot(context) ?: return@withContext false
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()

        true
    }

    /**
     * 屏幕截图
     */
    fun takeScreenShot(context: Context): Bitmap? {
        return TakeScreenshotApis.takeScreenshotNoThrow(context)
    }
}