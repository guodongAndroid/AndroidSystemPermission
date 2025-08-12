package com.guodong.android.system.permission.android.runtime

import android.content.Context

/**
 * Created by john.wick on 2025/5/27
 */
internal object RuntimePermissionCompat : IRuntimePermission {

    override fun grantRuntimePermission(context: Context, packageName: String): Boolean {
        return super.grantRuntimePermission(context, packageName)
    }
}