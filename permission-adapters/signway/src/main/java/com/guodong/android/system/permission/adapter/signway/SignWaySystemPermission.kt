package com.guodong.android.system.permission.adapter.signway

import android.content.Intent
import android.os.SystemProperties
import androidx.annotation.Keep
import com.guodong.android.system.permission.AospSystemPermission
import com.guodong.android.system.permission.Vendor

/**
 * Created by guodongAndroid on 2025/8/18
 */
@Keep
class SignWaySystemPermission : AospSystemPermission() {

    companion object {
        private const val PERSIST_NAVBAR_DISPLAY = "persist.navbar.display"
        private const val PERSIST_NAVBAR_TOUCH_UP_AUTO = "persist.navbar.touch_up_auto"

        private const val EXTRA_SHOW_NAVIGATION_BAR = "show_navigation_bar"
        private const val ACTION_NAVIGATION_DISPLAY = "android.intent.action.NAVIGATION_DISPLAY"
    }

    override fun getVendor(): String {
        return Vendor.SIGNWAY
    }

    override fun enableSystemBar(enable: Boolean) {
        SystemProperties.set(PERSIST_NAVBAR_TOUCH_UP_AUTO, if (enable) "true" else "false")
        val intent = Intent(ACTION_NAVIGATION_DISPLAY).apply {
            putExtra(EXTRA_SHOW_NAVIGATION_BAR, enable)
        }
        context.sendBroadcast(intent)
    }

    override fun isSystemBarEnabled(): Boolean {
        return SystemProperties.getBoolean(PERSIST_NAVBAR_DISPLAY, false)
    }
}