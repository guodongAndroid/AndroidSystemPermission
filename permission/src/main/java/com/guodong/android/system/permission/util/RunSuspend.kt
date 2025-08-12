package com.guodong.android.system.permission.util

/**
 * Created by john.wick on 2025/5/27
 */
internal class RunSuspend<T> {

    internal companion object {
        internal const val DEFAULT_AWAIT_TIMEOUT_DURATION = 3_000L
    }

    private var result: T? = null

    fun resumeWith(result: T) = synchronized(this) {
        this.result = result
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN") (this as Object).notifyAll()
    }

    fun await(): T = synchronized(this) {
        while (true) {
            when (val result = this.result) {
                null -> {
                    @Suppress(
                        "PLATFORM_CLASS_MAPPED_TO_KOTLIN"
                    )
                    (this as Object).wait()
                }
                else -> return result
            }
        }

        @Suppress("UNREACHABLE_CODE", "UNCHECKED_CAST") return result as T
    }

    fun await(timeout: Long): T? = synchronized(this) {
        require(timeout > 0L) { "timeout <= 0L" }

        while (true) {
            when (val result = this.result) {
                null -> {
                    @Suppress(
                        "PLATFORM_CLASS_MAPPED_TO_KOTLIN"
                    )
                    (this as Object).wait(timeout)
                    break
                }
                else -> return result
            }
        }

        @Suppress("UNCHECKED_CAST") return result as T
    }
}