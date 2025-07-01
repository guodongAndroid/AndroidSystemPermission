package com.guodong.android.system.permission.android.sntp

import android.content.Context
import android.os.Build
import androidx.annotation.WorkerThread
import java.util.concurrent.TimeUnit

/**
 * Created by john.wick on 2025/5/27
 */
internal object SntpClientCompat : ISntpClient {

    private val client = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            SntpClientApi33
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            SntpClientApi29
        }

        else -> {
            SntpClientApi24
        }
    }

    @WorkerThread
    override fun getNtpTime(
        context: Context,
        ntpServer: String,
        ntpPort: Int,
        timeout: Long,
        timeUnit: TimeUnit
    ): Long {
        return client.getNtpTime(context, ntpServer, ntpPort, timeout, timeUnit)
    }
}