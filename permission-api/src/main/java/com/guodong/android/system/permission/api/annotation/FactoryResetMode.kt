package com.guodong.android.system.permission.api.annotation

import androidx.annotation.IntDef
import androidx.annotation.Keep

/**
 * Created by guodongAndroid on 2025/8/29
 */
@IntDef(
    FactoryResetMode.USER_DATA,
    FactoryResetMode.FULL,
)
@Keep
@Retention(AnnotationRetention.SOURCE)
annotation class FactoryResetMode {
    @Keep
    companion object {
        /**
         * 仅用户安装的程序及程序数据
         */
        const val USER_DATA = 1

        /**
         * 包含本地存储的文件
         */
        const val FULL = 2
    }
}