package com.guodong.android.system.permission.app

import android.os.Build
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.guodong.android.system.permission.SystemPermissionCompat
import com.guodong.android.system.permission.Vendor
import com.guodong.android.system.permission.app.activities.AdbActivity
import com.guodong.android.system.permission.app.activities.ApplicationManagerActivity
import com.guodong.android.system.permission.app.activities.DeviceActivity
import com.guodong.android.system.permission.app.activities.EthernetActivity
import com.guodong.android.system.permission.app.activities.LauncherSettingsActivity
import com.guodong.android.system.permission.app.activities.NtpActivity
import com.guodong.android.system.permission.app.activities.ScreenBrightnessActivity
import com.guodong.android.system.permission.app.activities.ScreenRotationActivity
import com.guodong.android.system.permission.app.activities.SystemBarActivity
import com.guodong.android.system.permission.app.activities.SystemTimeActivity
import com.guodong.android.system.permission.app.activities.TakeScreenshotActivity
import com.guodong.android.system.permission.app.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivityMainBinding.initViews() {
        val osVersion =
            "Android ${Build.VERSION.RELEASE}(${Build.VERSION.CODENAME}) / API ${Build.VERSION.SDK_INT}"
        binding.tvOsVersion.text = getString(R.string.os_version, osVersion)
        binding.tvAppVersion.text = getString(R.string.app_version, getVersionName(packageName))

        binding.tvSdkVersion.text =
            getString(R.string.sdk_version, SystemPermissionCompat.getVersion())

        lifecycleScope.launch {
            binding.tvFirmwareVersion.text =
                getString(R.string.firmware_version, SystemPermissionCompat.getFirmwareVersion())
        }

        binding.btnEthernet.setOnClickListener { EthernetActivity.start(this@MainActivity) }
        binding.btnDevice.setOnClickListener { DeviceActivity.start(this@MainActivity) }
        binding.btnLauncher.setOnClickListener { LauncherSettingsActivity.start(this@MainActivity) }
        binding.btnScreenBrightness.setOnClickListener { ScreenBrightnessActivity.start(this@MainActivity) }
        binding.btnScreenRotation.setOnClickListener { ScreenRotationActivity.start(this@MainActivity) }
        binding.btnScreenShot.setOnClickListener { TakeScreenshotActivity.start(this@MainActivity) }
        binding.btnAdb.setOnClickListener { AdbActivity.start(this@MainActivity) }
        binding.btnSystemBar.setOnClickListener {
            if (SystemPermissionCompat.getVendor() == Vendor.AOSP) {
                Toast.makeText(this@MainActivity, "AOSP暂不不支持", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            SystemBarActivity.start(this@MainActivity)
        }
        binding.btnSystemTime.setOnClickListener { SystemTimeActivity.start(this@MainActivity) }
        binding.btnApplicationManager.setOnClickListener { ApplicationManagerActivity.start(this@MainActivity) }
        binding.btnNtpTime.setOnClickListener { NtpActivity.start(this@MainActivity) }
    }
}