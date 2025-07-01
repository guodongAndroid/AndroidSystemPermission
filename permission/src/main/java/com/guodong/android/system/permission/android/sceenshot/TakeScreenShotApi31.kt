package com.guodong.android.system.permission.android.sceenshot

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.DisplayAddress
import androidx.annotation.RequiresApi
import androidx.core.hardware.display.DisplayManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Created by john.wick on 2025/7/1
 *
 * Android12
 */
@RequiresApi(Build.VERSION_CODES.S)
internal object TakeScreenShotApi31 : ITakeScreenShot {

    private const val TAG = "TakeScreenShotApi31"

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
     * @see [https://cs.android.com/android/platform/superproject/+/android-12.0.0_r34:frameworks/base/packages/SystemUI/src/com/android/systemui/screenshot/ScreenshotController.java]
     */
    @Suppress("DEPRECATION")
    @SuppressLint("PrivateApi")
    override fun takeScreenShot(context: Context): Bitmap? {
        val display = DisplayManagerCompat.getInstance(context).getDisplay(Display.DEFAULT_DISPLAY)
            ?: return null
        val displayMetrics = DisplayMetrics()
        display.getRealMetrics(displayMetrics)
        val rect = Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val width = rect.width()
        val height = rect.height()

        return try {
            val address = display.getAddress()
            if (address !is DisplayAddress.Physical) {
                return null
            }

            val clazz = Class.forName("android.view.SurfaceControl")

            val displayCaptureArgsClazz =
                Class.forName("android.view.SurfaceControl${'$'}DisplayCaptureArgs")

            val screenshotBufferClazz =
                Class.forName("android.view.SurfaceControl${'$'}ScreenshotHardwareBuffer")

            val captureArgs = getCaptureArgs(clazz, address, rect, width, height) ?: return null

            // public static ScreenshotHardwareBuffer captureDisplay(DisplayCaptureArgs captureArgs)
            val captureDisplay = clazz.getDeclaredMethod("captureDisplay", displayCaptureArgsClazz)
            captureDisplay.isAccessible = true

            val screenshotBuffer = captureDisplay.invoke(null, captureArgs) ?: return null

            // public Bitmap asBitmap()
            val asBitmap = screenshotBufferClazz.getDeclaredMethod("asBitmap")
            asBitmap.isAccessible = true

            return (asBitmap.invoke(screenshotBuffer) as? Bitmap)?.apply {
                setHasAlpha(false)
            }
        } catch (e: Exception) {
            Log.e(TAG, "takeScreenShot: ${e.message}", e)
            null
        }
    }

    /**
     * @return SurfaceControl.DisplayCaptureArgs
     */
    @SuppressLint("PrivateApi")
    private fun getCaptureArgs(
        clazz: Class<*>,
        address: DisplayAddress.Physical,
        rect: Rect,
        width: Int,
        height: Int
    ): Any? {
        // SurfaceControl public static IBinder getPhysicalDisplayToken(long physicalDisplayId)
        val getPhysicalDisplayToken =
            clazz.getDeclaredMethod("getPhysicalDisplayToken", Long::class.java)
        getPhysicalDisplayToken.isAccessible = true
        val displayToken =
            getPhysicalDisplayToken.invoke(null, address.physicalDisplayId) ?: return null

        val builderClazz =
            Class.forName("android.view.SurfaceControl${'$'}DisplayCaptureArgs${'$'}Builder")

        // public Builder(IBinder displayToken)
        val builderConstructor = builderClazz.getDeclaredConstructor(IBinder::class.java)
        builderConstructor.isAccessible = true

        // public T setSourceCrop(Rect sourceCrop)
        val setSourceCrop = builderClazz.getMethod("setSourceCrop", Rect::class.java)
        setSourceCrop.isAccessible = true

        // public Builder setSize(int width, int height)
        val setSize = builderClazz.getMethod("setSize", Int::class.java, Int::class.java)
        setSize.isAccessible = true

        // public DisplayCaptureArgs build()
        val build = builderClazz.getMethod("build")
        build.isAccessible = true

        val builder = builderConstructor.newInstance(displayToken)
        setSourceCrop.invoke(builder, rect)
        setSize.invoke(builder, width, height)
        return build.invoke(builder)
    }

    @SuppressLint("PrivateApi")
    private fun Display.getAddress(): DisplayAddress {
        val clazz = this.javaClass
        // public DisplayAddress getAddress()
        val getAddress = clazz.getDeclaredMethod("getAddress")
        getAddress.isAccessible = true
        return getAddress.invoke(this) as DisplayAddress
    }
}