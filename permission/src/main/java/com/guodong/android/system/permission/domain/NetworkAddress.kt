package com.guodong.android.system.permission.domain

import androidx.annotation.Keep

/**
 * Created by john.wick on 2025/5/27
 */
@Keep
data class NetworkAddress(
    val isDhcp: Boolean,
    val address: String,
    val subnetMask: String,
    val gateway: String,
    val primaryDNS: String,
    val secondaryDNS: String
) {
    companion object {
        internal val EMPTY = NetworkAddress(
            false,
            "0.0.0.0",
            "0.0.0.0",
            "0.0.0.0",
            "0.0.0.0",
            "0.0.0.0",
        )
    }
}
