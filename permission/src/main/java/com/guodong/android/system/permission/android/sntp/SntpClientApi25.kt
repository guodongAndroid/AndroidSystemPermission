package com.guodong.android.system.permission.android.sntp

import android.content.Context
import android.net.SntpClient
import android.os.SystemClock
import androidx.annotation.WorkerThread
import java.util.concurrent.TimeUnit

/**
 * Created by john.wick on 2025/5/27
 */
internal object SntpClientApi25 : ISntpClient {

    private val client = SntpClient()

    @WorkerThread
    override fun getNtpTime(
        context: Context,
        ntpServer: String,
        ntpPort: Int,
        timeout: Long,
        timeUnit: TimeUnit
    ): Long {
        if (client.requestTime(ntpServer, timeUnit.toMillis(timeout).toInt())) {
            return client.ntpTime + SystemClock.elapsedRealtime() - client.ntpTimeReference
        }

        return -1
    }
}