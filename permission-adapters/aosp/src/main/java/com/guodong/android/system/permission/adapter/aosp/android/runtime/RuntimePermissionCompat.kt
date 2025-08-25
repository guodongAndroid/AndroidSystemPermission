package com.guodong.android.system.permission.adapter.aosp.android.runtime

import android.content.Context

/**
 * Created by guodongAndroid on 2025/5/27
 */
internal object RuntimePermissionCompat : IRuntimePermission {

    override fun grantRuntimePermission(context: Context, packageName: String): Boolean {
        return super.grantRuntimePermission(context, packageName)
    }
}