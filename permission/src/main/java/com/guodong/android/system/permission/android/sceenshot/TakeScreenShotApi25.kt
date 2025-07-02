package com.guodong.android.system.permission.android.sceenshot

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import androidx.core.content.getSystemService
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.abs

/**
 * Created by john.wick on 2025/6/30
 */
internal object TakeScreenShotApi25 : ITakeScreenShot {

    private const val TAG = "TakeScreenShotApi25"

    override suspend fun takeScreenShot(context: Context, savePath: String): Boolean =
        withContext(Dispatchers.IO) {
            val file = File(savePath)
            if (file.isDirectory) {
                throw IllegalArgumentException("savePath is directory")
            }

            if (!file.exists()) {
                val parentFile =
                    file.parentFile ?: throw IllegalArgumentException("savePath is no parent path")
                if (!parentFile.exists()) {
                    parentFile.mkdirs()
                }
                file.createNewFile()
            }

            val bitmap =
                takeScreenShot(context) ?: throw RuntimeException("take screenshot failed.")

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
        val matrix = Matrix()
        val display = manager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getRealMetrics(displayMetrics)
        val dims = floatArrayOf(
            displayMetrics.widthPixels.toFloat(),
            displayMetrics.heightPixels.toFloat()
        )
        val degrees = getDegreesForRotation(display.rotation)
        val requiresRotation = degrees > 0
        if (requiresRotation) {
            matrix.reset()
            matrix.preRotate(-degrees.toFloat())
            matrix.mapPoints(dims)
            dims[0] = abs(dims[0])
            dims[1] = abs(dims[1])
        }

        return try {
            val clazz = Class.forName("android.view.SurfaceControl")
            // public static Bitmap screenshot(int width, int height)
            val screenshot =
                clazz.getDeclaredMethod("screenshot", Int::class.java, Int::class.java)
            screenshot.isAccessible = true
            var bitmap =
                screenshot.invoke(null, dims[0].toInt(), dims[1].toInt()) as? Bitmap ?: return null

            if (requiresRotation) {
                val ss = createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels)
                val canvas = Canvas(ss)
                canvas.translate((ss.width / 2).toFloat(), (ss.height / 2).toFloat())
                canvas.rotate(degrees.toFloat())
                canvas.translate(-dims[0] / 2, -dims[1] / 2)
                canvas.drawBitmap(bitmap, 0F, 0F, null)
                canvas.setBitmap(null)
                bitmap.recycle()
                bitmap = ss
            }

            bitmap.setHasAlpha(false)
            // bitmap.prepareToDraw()

            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "takeScreenShot: ${e.message}", e)
            null
        }
    }

    private fun getDegreesForRotation(value: Int): Int {
        return when (value) {
            Surface.ROTATION_90 -> 360 - 90
            Surface.ROTATION_180 -> 360 - 180
            Surface.ROTATION_270 -> 360 - 270
            else -> 0
        }
    }
}