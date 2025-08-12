package com.guodong.android.system.permission.android.power

/**
 * Created by john.wick on 2025/6/30
 */
internal object PowerCompat : IPower {

    override fun reboot() {
        super.reboot()
    }

    override fun shutdown() {
        super.shutdown()
    }

    override fun goToSleep() {
        super.goToSleep()
    }

    override fun wakeUp() {
        super.wakeUp()
    }
}