package com.guodong.android.system.permission.android.sceenshot

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.abs
import androidx.core.graphics.createBitmap

/**
 * Created by john.wick on 2025/7/1
 *
 * Android9
 */
@RequiresApi(Build.VERSION_CODES.P)
internal object TakeScreenShotApi28 : ITakeScreenShot {

    private const val TAG = "TakeScreenShotApi28"

    override suspend fun takeScreenShot(context: Context, savePath: String): Boolean =
        withContext(Dispatchers.IO) {
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

    @Suppress("DEPRECATION")
    @SuppressLint("PrivateApi")
    override fun takeScreenShot(context: Context): Bitmap? {
        val manager = context.getSystemService<WindowManager>() ?: return null
        val display = manager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getRealMetrics(displayMetrics)
        val rect = Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val rotation = display.rotation
        val width = rect.width()
        val height = rect.height()

        return try {
            val clazz = Class.forName("android.view.SurfaceControl")
            // public static Bitmap screenshot(Rect sourceCrop, int width, int height, int rotation)
            val screenshot =
                clazz.getDeclaredMethod("screenshot", Rect::class.java, Int::class.java, Int::class.java, Int::class.java)
            screenshot.isAccessible = true
            val bitmap =
                screenshot.invoke(null, rect, width, height, rotation) as? Bitmap ?: return null

            bitmap.setHasAlpha(false)
            // bitmap.prepareToDraw()

            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "takeScreenShot: ${e.message}", e)
            null
        }
    }
}