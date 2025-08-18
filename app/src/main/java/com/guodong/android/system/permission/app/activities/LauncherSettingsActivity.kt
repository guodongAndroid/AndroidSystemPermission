package com.guodong.android.system.permission.app.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.guodong.android.system.permission.api.SystemPermissionCompat
import com.guodong.android.system.permission.app.BaseActivity
import com.guodong.android.system.permission.app.R
import com.guodong.android.system.permission.app.databinding.ActivityLauncherSettingsBinding
import com.guodong.android.system.permission.app.openHomeSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by guodongAndroid on 2025/8/13
 */
class LauncherSettingsActivity : BaseActivity<ActivityLauncherSettingsBinding>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LauncherSettingsActivity::class.java))
        }
    }

    override fun getViewBinding(): ActivityLauncherSettingsBinding {
        return ActivityLauncherSettingsBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivityLauncherSettingsBinding.initViews() {
        btnGetLauncher.setOnClickListener {
            refreshLauncherUI()
        }

        btnSetLauncher.setOnClickListener {
            val launcher = SystemPermissionCompat.setLauncher(packageName)
            Toast.makeText(
                this@LauncherSettingsActivity,
                "Set Launcher: $launcher",
                Toast.LENGTH_SHORT
            ).show()

            if (launcher) {
                lifecycleScope.launch {
                    delay(1000)
                    refreshLauncherUI()
                }
            }
        }

        btnOpenLauncherSettings.setOnClickListener {
            openHomeSettings()
        }

        btnOpenSystemLauncher.setOnClickListener {
            SystemPermissionCompat.openSystemLauncher()
        }

        btnOpenSystemSettings.setOnClickListener {
            SystemPermissionCompat.openSystemSettings()
        }

        btnOpenDevelopmentSettings.setOnClickListener {
            SystemPermissionCompat.openSystemDevelopmentSettings()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.refreshLauncherUI()
    }

    private fun ActivityLauncherSettingsBinding.refreshLauncherUI() {
        val name = SystemPermissionCompat.getLauncher()
        tvLauncherName.text =
            getString(R.string.launcher_component_name, name?.flattenToShortString() ?: "无")
    }
}