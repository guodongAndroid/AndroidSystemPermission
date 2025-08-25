package com.guodong.android.system.permission.api.annotation

import androidx.annotation.IntDef
import androidx.annotation.Keep

/**
 * Created by guodongAndroid on 2025/7/2
 */
@Keep
@Target(AnnotationTarget.TYPE)
@IntDef(
    EthernetState.CONNECTING,
    EthernetState.CONNECTED,
    EthernetState.DISCONNECTING,
    EthernetState.DISCONNECTED,
)
annotation class EthernetState {
    @Keep
    companion object {
        /**
         * 连接中
         */
        const val CONNECTING = 1

        /**
         * 已连接
         */
        const val CONNECTED = 2

        /**
         * 断开连接中
         */
        const val DISCONNECTING = 3

        /**
         * 已断开连接
         */
        const val DISCONNECTED = 4
    }
}