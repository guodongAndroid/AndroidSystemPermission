package com.guodong.android.system.permission.app

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by guodongAndroid on 2025/8/5
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    companion object {
        private const val TAG = "BaseActivity"
    }

    protected val binding by lazy { getViewBinding() }

    @Suppress("MemberVisibilityCanBePrivate", "PropertyName")
    protected var isResumed_: Boolean = false
    private var idleJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isEnableEdgeToEdge()) {
            enableEdgeToEdge()
        }

        if (isKeepScreenOn()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        setContentView(binding.root)

        if (isPaddingSystemBar()) {
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        binding.initViews()
    }

    protected open fun isKeepScreenOn() = true
    protected open fun isEnableEdgeToEdge() = false
    protected open fun isPaddingSystemBar() = false

    protected open fun isIdleEnabled(): Boolean = false

    protected open fun isTouchIdleEnabled(): Boolean = false

    protected open fun isResumeIdleEnabled(): Boolean = false

    /**
     * 默认30秒无操作认为进入IDLE状态
     */
    protected open fun idleThresholdMs(): Long {
        return if (BuildConfig.DEBUG) 1_000L * 10 else 1_000L * 30
    }

    @CallSuper
    protected open suspend fun onIdle() {
    }

    protected fun triggerIdle() {
        createIdleJob()
    }

    abstract fun getViewBinding(): VB
    abstract fun VB.initViews()

    override fun onResume() {
        super.onResume()

        isResumed_ = true
        createResumeIdleEnabled()
    }

    override fun onPause() {
        super.onPause()

        isResumed_ = false
        cancelIdleJob()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            createIdleJob()
        } else {
            cancelIdleJob()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        cancelIdleJob()

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                hideKeyboard(ev)
            }

            MotionEvent.ACTION_UP -> {
                createAutoIdleJob()
            }

            MotionEvent.ACTION_CANCEL -> {
                createAutoIdleJob()
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(ev: MotionEvent) {
        val v = currentFocus
        if (v is EditText) {
            val outRect = Rect()
            v.getGlobalVisibleRect(outRect)
            if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                v.hideKeyboard()
            }
        }
    }

    private fun createAutoIdleJob() {
        if (!isTouchIdleEnabled()) {
            return
        }

        createIdleJob()
    }

    private fun createResumeIdleEnabled() {
        if (!isResumeIdleEnabled()) {
            return
        }

        createIdleJob()
    }

    private fun createIdleJob() {
        cancelIdleJob()

        if (!isResumed_) {
            return
        }

        idleJob = lifecycleScope.launch {
            delay(idleThresholdMs())

            if (isIdleEnabled() && isResumed_ && hasWindowFocus()) {
                onIdle()
            }
        }
    }

    private fun cancelIdleJob() {
        idleJob?.cancel()
        idleJob = null
    }

    override fun onDestroy() {
        cancelIdleJob()

        super.onDestroy()
    }
}