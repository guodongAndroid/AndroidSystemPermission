package com.guodong.android.system.permission.android.pm

import android.content.IIntentReceiver
import android.content.IIntentSender
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.IBinder
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.TimeUnit

/**
 * Created by john.wick on 2025/5/27
 */
internal class LocalIntentReceiver {

    private val result = SynchronousQueue<Intent>()

    private val localSender = object : IIntentSender.Stub() {
        override fun send(
            code: Int,
            intent: Intent,
            resolvedType: String?,
            whitelistToken: IBinder?,
            finishedReceiver: IIntentReceiver?,
            requiredPermission: String?,
            options: Bundle?
        ) {
            try {
                result.offer(intent, 5, TimeUnit.SECONDS)
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException(e)
            }
        }
    }

    fun getIntentSender(): IntentSender {
        val clazz = IntentSender::class.java
        val constructor = clazz.getConstructor(IIntentSender::class.java)
        constructor.isAccessible = true
        return constructor.newInstance(localSender)
    }

    fun getResult(): Intent {
        return try {
            result.take()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }
}