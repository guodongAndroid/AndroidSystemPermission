package com.guodong.android.system.permission.domain

import androidx.annotation.Keep

/**
 * Created by john.wick on 2025/5/27
 */
@Keep
data class NetworkAddress(
    val isStatic: Boolean,
    val address: String,
    val netmask: String,
    val gateway: String,
    val dns1: String,
    val dns2: String,
    val mac: String,
) {
    companion object {
        internal val EMPTY = NetworkAddress(
            false,
            "0.0.0.0",
            "0.0.0.0",
            "0.0.0.0",
            "0.0.0.0",
            "0.0.0.0",
            "",
        )
    }
}
