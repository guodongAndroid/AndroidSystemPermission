package com.guodong.android.system.permission.adapter.aosp.android.sntp

import android.content.Context
import androidx.annotation.WorkerThread
import java.util.concurrent.TimeUnit

/**
 * Created by john.wick on 2025/5/27
 */
internal interface ISntpClient {

    @WorkerThread
    fun getNtpTime(
        context: Context,
        ntpServer: String,
        ntpPort: Int,
        timeout: Long,
        timeUnit: TimeUnit,
    ): Long
}