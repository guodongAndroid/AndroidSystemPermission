package com.guodong.android.system.permission.adapter.aosp.android.sntp

import android.content.Context
import android.net.ConnectivityManager
import android.net.SntpClient
import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import java.util.concurrent.TimeUnit

/**
 * Created by john.wick on 2025/5/27
 */
@RequiresApi(Build.VERSION_CODES.Q)
internal object SntpClientApi29 : ISntpClient {

    private val client = SntpClient()

    @WorkerThread
    override fun getNtpTime(
        context: Context,
        ntpServer: String,
        ntpPort: Int,
        timeout: Long,
        timeUnit: TimeUnit
    ): Long {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        val network = manager.activeNetwork ?: return -1

        if (client.requestTime(ntpServer, timeUnit.toMillis(timeout).toInt(), network)) {
            return client.ntpTime + SystemClock.elapsedRealtime() - client.ntpTimeReference
        }

        return -1
    }
}