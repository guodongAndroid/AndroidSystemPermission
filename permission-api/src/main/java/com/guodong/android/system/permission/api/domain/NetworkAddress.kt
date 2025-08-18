package com.guodong.android.system.permission.api.domain

import androidx.annotation.IntDef
import androidx.annotation.Keep

/**
 * Created by john.wick on 2025/5/27
 */
@Keep
data class NetworkAddress(
    @IpAssignment val ipAssignment: Int,
    val address: String,
    val netmask: String,
    val gateway: String,
    val dns1: String,
    val dns2: String?,
) {
    @Keep
    companion object {
        val UNASSIGNED = NetworkAddress(
            IpAssignment.UNASSIGNED,
            "0.0.0.0",
            "0.0.0.0",
            "0.0.0.0",
            "0.0.0.0",
            "0.0.0.0",
        )
    }

    @Keep
    @IntDef(
        IpAssignment.STATIC,
        IpAssignment.DHCP,
        IpAssignment.UNASSIGNED,
    )
    annotation class IpAssignment {
        @Keep
        companion object {
            const val STATIC = 1
            const val DHCP = 2
            const val UNASSIGNED = 3
        }
    }
}
