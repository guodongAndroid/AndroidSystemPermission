package com.guodong.android.system.permission.app.model

import android.graphics.drawable.Drawable

/**
 * Created by guodongAndroid on 2025/8/15
 */
data class ApplicationModel(
    val packageName: String,
    val icon: Drawable,
    val name: String,
    val version: String,
    val isSystem: Boolean,
)