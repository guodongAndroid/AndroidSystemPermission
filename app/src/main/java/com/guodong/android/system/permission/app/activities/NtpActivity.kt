package com.guodong.android.system.permission.app.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.guodong.android.system.permission.SystemPermissionCompat
import com.guodong.android.system.permission.app.BaseActivity
import com.guodong.android.system.permission.app.R
import com.guodong.android.system.permission.app.databinding.ActivityNtpBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Created by guodongAndroid on 2025/8/15
 */
class NtpActivity : BaseActivity<ActivityNtpBinding>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, NtpActivity::class.java))
        }
    }

    override fun getViewBinding(): ActivityNtpBinding {
        return ActivityNtpBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivityNtpBinding.initViews() {
        etNtpServer.doAfterTextChanged {
            refreshUI()
        }

        lifecycleScope.launch {
            while (isActive) {
                val dateTime = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(System.currentTimeMillis())
                tvTime.text = getString(R.string.current_date_time, dateTime)
                delay(1_000L)
            }
        }

        btnGetNtpTime.setOnClickListener {
            lifecycleScope.launch {
                it.isEnabled = false
                val ntpTime = getNtpTime(etNtpServer.text!!.toString())
                if (ntpTime < 0) {
                    it.isEnabled = true
                    Toast.makeText(this@NtpActivity, "Get failed", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val format =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(ntpTime)

                tvNtpTime.text = getString(R.string.got_ntp_time, format)
                it.isEnabled = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.refreshUI()
    }

    private fun ActivityNtpBinding.refreshUI() {
        btnGetNtpTime.isEnabled = !etNtpServer.text.isNullOrEmpty()
    }

    private suspend fun getNtpTime(server: String): Long {
        return SystemPermissionCompat.getNtpTime(server, 123, 5, TimeUnit.SECONDS)
    }
}