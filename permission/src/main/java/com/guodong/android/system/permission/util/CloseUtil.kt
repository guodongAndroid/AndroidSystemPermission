package com.guodong.android.system.permission.util

import java.io.Closeable
import java.io.IOException

/**
 * Created by john.wick on 2025/5/27
 */
object CloseUtil {

    /**
     * 关闭IO
     *
     * @param closeables closeables
     */
    fun closeIO(vararg closeables: Closeable?) {
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 安静关闭IO
     *
     * @param closeables closeables
     */
    fun closeIOQuietly(vararg closeables: Closeable?) {
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (ignored: IOException) {
                }
            }
        }
    }
}