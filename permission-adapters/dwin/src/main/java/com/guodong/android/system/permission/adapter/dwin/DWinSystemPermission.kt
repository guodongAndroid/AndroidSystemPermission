package com.guodong.android.system.permission.adapter.dwin

import android.content.Intent
import android.provider.Settings
import androidx.annotation.Keep
import com.guodong.android.system.permission.AospSystemPermission
import com.guodong.android.system.permission.Vendor

/**
 * Created by guodongAndroid on 2025/8/15
 */
@Keep
class DWinSystemPermission : AospSystemPermission() {

    companion object {
        private const val PERSIST_SYS_NAVIGATIONBAR_ENABLE = "persist.sys.navigationbar.enable"
        private const val PERSIST_SYS_STATUSBAR_ENABLE = "persist.sys.statusbar.enable"
        private const val PERSIST_SYS_EXPLAN_ENABLE = "persist.sys.explan.enable"

        private const val ACTION_SYS_NAVIGATIONBAR_SHOW = "sys.navigationbar.show"
        private const val ACTION_SYS_NAVIGATIONBAR_HIDE = "sys.navigationbar.hide"

        private const val ACTION_SYS_STATUSBAR_SHOW = "sys.statusbar.show"
        private const val ACTION_SYS_STATUSBAR_HIDE = "sys.statusbar.hide"

        private const val ACTION_SYS_EXPLAN_SHOW = "sys.explan.show"
        private const val ACTION_SYS_EXPLAN_HIDE = "sys.explan.hide"
    }

    @Vendor
    override fun getVendor(): String {
        return Vendor.DWIN
    }

    override fun enableSystemBar(enable: Boolean) {
        if (enable) {
            showSystemBar()
        } else {
            hideSystemBar()
        }
    }

    override fun isSystemBarEnabled(): Boolean {
        val isStatusBarEnabled =
            Settings.Global.getInt(
                context.contentResolver,
                PERSIST_SYS_STATUSBAR_ENABLE, 0
            ) == 1

        val isNavigationBarEnabled =
            Settings.Global.getInt(
                context.contentResolver,
                PERSIST_SYS_NAVIGATIONBAR_ENABLE,
                0
            ) == 1

        val isExplanEnabled =
            Settings.Global.getInt(
                context.contentResolver,
                PERSIST_SYS_EXPLAN_ENABLE,
                0
            ) == 1

        return isStatusBarEnabled && isNavigationBarEnabled && isExplanEnabled
    }

    private fun hideSystemBar() {
        val statusBarIntent = Intent(ACTION_SYS_STATUSBAR_HIDE)
        val navigationBarIntent = Intent(ACTION_SYS_NAVIGATIONBAR_HIDE)
        val explanIntent = Intent(ACTION_SYS_EXPLAN_HIDE)

        context.sendBroadcast(statusBarIntent)
        context.sendBroadcast(navigationBarIntent)
        context.sendBroadcast(explanIntent)
    }

    private fun showSystemBar() {
        val statusBarIntent = Intent(ACTION_SYS_STATUSBAR_SHOW)
        val navigationBarIntent = Intent(ACTION_SYS_NAVIGATIONBAR_SHOW)
        val explanIntent = Intent(ACTION_SYS_EXPLAN_SHOW)

        context.sendBroadcast(statusBarIntent)
        context.sendBroadcast(navigationBarIntent)
        context.sendBroadcast(explanIntent)
    }
}