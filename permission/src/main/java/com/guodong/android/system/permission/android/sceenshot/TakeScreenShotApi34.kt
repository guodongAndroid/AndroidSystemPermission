package com.guodong.android.system.permission.android.sceenshot

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.IBinder
import android.os.ServiceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.DisplayAddress
import android.view.IWindowManager
import android.window.ScreenCapture
import android.window.ScreenCapture.CaptureArgs
import androidx.annotation.RequiresApi
import androidx.core.hardware.display.DisplayManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Created by john.wick on 2025/7/1
 *
 * Android14
 */
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
internal object TakeScreenShotApi34 : ITakeScreenShot {

    private const val TAG = "TakeScreenShotApi34"

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

            val bitmap = takeScreenShot(context) ?: return@withContext false
            file.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            bitmap.recycle()

            true
        }

    /**
     * @see [https://cs.android.com/android/platform/superproject/+/android-14.0.0_r75:frameworks/base/packages/SystemUI/src/com/android/systemui/screenshot/ScreenshotController.java#handleScreenshot]
     * @see [https://cs.android.com/android/platform/superproject/+/android-14.0.0_r75:frameworks/base/packages/SystemUI/src/com/android/systemui/screenshot/ImageCaptureImpl.kt]
     */
    @Suppress("DEPRECATION")
    @SuppressLint("PrivateApi")
    override fun takeScreenShot(context: Context): Bitmap? {
        val display = DisplayManagerCompat.getInstance(context).getDisplay(Display.DEFAULT_DISPLAY)
            ?: return null
        val displayMetrics = DisplayMetrics()
        display.getRealMetrics(displayMetrics)
        val rect = Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)

        return try {
            val manager =
                IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE))
            val captureArgs = CaptureArgs.Builder().setSourceCrop(rect).build()
            val syncCaptureListener = ScreenCapture.createSyncCaptureListener()
            manager.captureDisplay(display.displayId, captureArgs, syncCaptureListener)
            val buffer = syncCaptureListener.buffer
            buffer?.asBitmap()?.apply {
                setHasAlpha(false)
            }
        } catch (e: Exception) {
            Log.e(TAG, "takeScreenShot: ${e.message}", e)
            null
        }
    }
}