package com.guodong.android.system.permission.app.model

/**
 * Created by guodongAndroid on 2025/8/14
 */
data class TimeZoneModel(
    val id: String,
    val shortName: String,
    val longName: String,
    var isSelected: Boolean = false,
) {
    override fun toString(): String {
        return "$id $shortName $longName"
    }
}
