package com.guodong.android.system.permission.api.annotation

import androidx.annotation.IntDef
import androidx.annotation.Keep

/**
 * Created by guodongAndroid on 2025/5/27
 */
@Keep
@IntDef(
    Rotation.ROTATION_0,
    Rotation.ROTATION_90,
    Rotation.ROTATION_180,
    Rotation.ROTATION_270,
)
@Retention(AnnotationRetention.SOURCE)
annotation class Rotation {
    @Keep
    companion object {
        const val ROTATION_0 = 0
        const val ROTATION_90 = 1
        const val ROTATION_180 = 2
        const val ROTATION_270 = 3
    }
}
