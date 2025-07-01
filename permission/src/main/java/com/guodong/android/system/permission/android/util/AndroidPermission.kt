package com.guodong.android.system.permission.android.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.UserHandle

/**
 * Created by john.wick on 2025/5/27
 */

/**
 * PackageManager的名称
 */
internal const val PACKAGE_SERVICE = "package"

/**
 * @see [android.os.UserHandle]
 */
internal const val USER_SYSTEM_ID = 0

/**
 * @see [android.os.UserHandle]
 */
internal const val PER_USER_RANGE: Int = 100000

@get:SuppressLint("PrivateApi")
val UserHandle.userId: Int
    get() = try {
        val clazz = this.javaClass
        val getIdentifier = clazz.getMethod("getIdentifier")
        getIdentifier.isAccessible = true
        getIdentifier.invoke(this) as Int
    } catch (e: Exception) {
        USER_SYSTEM_ID
    }

@get:SuppressLint("PrivateApi")
val Context.userId: Int
    get() = try {
        val clazz = this.javaClass
        val getUserId = clazz.getMethod("getUserId")
        getUserId.isAccessible = true
        getUserId.invoke(this) as Int
    } catch (e: Exception) {
        USER_SYSTEM_ID
    }