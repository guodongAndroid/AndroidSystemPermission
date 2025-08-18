package com.guodong.android.system.permission.api

import androidx.annotation.Keep
import androidx.annotation.StringDef

/**
 * Created by guodongAndroid on 2025/8/14
 */
@MustBeDocumented
@StringDef(
    Vendor.AOSP,
    Vendor.HIKVISION,
    Vendor.SIGNWAY,
    Vendor.DWIN,
)
@Keep
@Retention(AnnotationRetention.BINARY)
annotation class Vendor {
    @Keep
    companion object {
        /**
         * 原生AOSP
         */
        const val AOSP = "aosp"

        /**
         * 海康威视
         */
        const val HIKVISION = "hikvision"

        /**
         * 欣威视通
         */
        const val SIGNWAY = "signway"

        /**
         * 迪文
         */
        const val DWIN = "dwin"
    }
}
