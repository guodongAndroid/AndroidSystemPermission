package com.guodong.android.system.permission.app.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.guodong.android.system.permission.api.SystemPermissionCompat
import com.guodong.android.system.permission.app.BaseActivity
import com.guodong.android.system.permission.app.R
import com.guodong.android.system.permission.app.databinding.ActivityAdbBinding

/**
 * Created by guodongAndroid on 2025/8/14
 */
class AdbActivity : BaseActivity<ActivityAdbBinding>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, AdbActivity::class.java))
        }
    }

    override fun getViewBinding(): ActivityAdbBinding {
        return ActivityAdbBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivityAdbBinding.initViews() {
        btnOpenDevelopmentSettings.setOnClickListener {
            SystemPermissionCompat.openSystemDevelopmentSettings()
        }

        msAdb.setOnCheckedChangeListener { _, isChecked ->
            SystemPermissionCompat.enableAdb(isChecked)
            refreshAdbPortStatusUI(isChecked)
        }

        etAdbPort.doAfterTextChanged {
            refreshAdbPortStatusUI(true)
        }

        btnGetAdbPort.setOnClickListener {
            refreshAdbPortUI()
        }

        btnSetAdbPort.setOnClickListener {
            val port = etAdbPort.text?.toString()?.toInt() ?: return@setOnClickListener
            if (port !in 5000 .. 65535) {
                Toast.makeText(
                    this@AdbActivity,
                    "The port MUST be between 5000 and 65535.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            SystemPermissionCompat.setAdbPort(port)

            refreshAdbPortUI()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.refreshUI()
    }

    private fun ActivityAdbBinding.refreshUI() {
        val isAdbEnabled = SystemPermissionCompat.isAdbEnabled()

        refreshAdbPortUI()
        refreshAdbPortStatusUI(isAdbEnabled)
        msAdb.isChecked = isAdbEnabled
    }

    private fun ActivityAdbBinding.refreshAdbPortUI() {
        val adbPort = SystemPermissionCompat.getAdbPort()
        tvAdbPort.text = getString(R.string.current_adb_port, adbPort)
        etAdbPort.setText(adbPort.toString())
        etAdbPort.setSelection(adbPort.toString().length)
    }

    private fun ActivityAdbBinding.refreshAdbPortStatusUI(isAdbEnabled: Boolean) {
        etAdbPort.isEnabled = isAdbEnabled
        btnGetAdbPort.isEnabled = isAdbEnabled
        btnSetAdbPort.isEnabled = isAdbEnabled && ((etAdbPort.text?.length ?: 0) >= 4)
    }
}