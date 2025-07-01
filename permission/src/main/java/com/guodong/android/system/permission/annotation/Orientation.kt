package com.guodong.android.system.permission.annotation

import androidx.annotation.IntDef
import androidx.annotation.Keep

/**
 * Created by john.wick on 2025/5/27
 */
@Keep
@IntDef(
    Orientation.ORIENTATION_0,
    Orientation.ORIENTATION_90,
    Orientation.ORIENTATION_180,
    Orientation.ORIENTATION_270,
)
annotation class Orientation {
    @Keep
    companion object {
        const val ORIENTATION_0 = 0
        const val ORIENTATION_90 = 90
        const val ORIENTATION_180 = 180
        const val ORIENTATION_270 = 270
    }
}
