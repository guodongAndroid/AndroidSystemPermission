package com.guodong.android.system.permission.app.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.guodong.android.system.permission.SystemPermissionCompat
import com.guodong.android.system.permission.app.BaseActivity
import com.guodong.android.system.permission.app.databinding.ActivityApplicationDetailBinding
import com.guodong.android.system.permission.app.getApplicationModel
import com.guodong.android.system.permission.app.openApplicationDetailsSettings

/**
 * Created by guodongAndroid on 2025/8/15
 */
class ApplicationDetailActivity : BaseActivity<ActivityApplicationDetailBinding>() {

    companion object {
        private const val EXTRA_PACKAGE_NAME = "extra_package_name"

        fun start(context: Context, packageName: String) {
            val intent = Intent(context, ApplicationDetailActivity::class.java).apply {
                putExtra(EXTRA_PACKAGE_NAME, packageName)
            }
            context.startActivity(intent)
        }
    }

    override fun getViewBinding(): ActivityApplicationDetailBinding {
        return ActivityApplicationDetailBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivityApplicationDetailBinding.initViews() {
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        if (packageName == null) {
            finish()
            return
        }

        val model = getApplicationModel(packageName)
        if (model == null) {
            finish()
            return
        }

        binding.ivAppIcon.setImageDrawable(model.icon)
        binding.tvAppName.text = model.name
        binding.tvAppVersion.text = model.version
        binding.tvAppPackageName.text = model.packageName

        btnOpenDetail.setOnClickListener {
            openApplicationDetailsSettings(packageName)
        }

        btnUninstall.setOnClickListener {
            AlertDialog.Builder(this@ApplicationDetailActivity)
                .setTitle("确认要卸载 ${model.name} 么？")
                .setPositiveButton("确认") { dialog, _ ->
                    SystemPermissionCompat.uninstallPackage(
                        packageName
                    ) { _, isSuccessful, _, _, _ ->
                        runOnUiThread {
                            Toast.makeText(
                                this@ApplicationDetailActivity,
                                "卸载: $isSuccessful",
                                Toast.LENGTH_SHORT
                            ).show()

                            if (isSuccessful) {
                                finish()
                            }
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        btnKill.setOnClickListener {
            AlertDialog.Builder(this@ApplicationDetailActivity)
                .setTitle("确认要Kill ${model.name} 么？")
                .setPositiveButton("确认") { dialog, _ ->
                    SystemPermissionCompat.killBackgroundProcesses(packageName)
                    dialog.dismiss()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        btnForceStop.setOnClickListener {
            AlertDialog.Builder(this@ApplicationDetailActivity)
                .setTitle("确认要强制停止 ${model.name} 么？")
                .setPositiveButton("确认") { dialog, _ ->
                    SystemPermissionCompat.forceStopPackage(packageName)
                    dialog.dismiss()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        btnClear.setOnClickListener {
            AlertDialog.Builder(this@ApplicationDetailActivity)
                .setTitle("确认要清空 ${model.name} 用户数据么？")
                .setPositiveButton("确认") { dialog, _ ->
                    SystemPermissionCompat.clearApplicationUserData(packageName) { _, isSuccessful ->
                        runOnUiThread {
                            Toast.makeText(
                                this@ApplicationDetailActivity,
                                "清空: $isSuccessful",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        btnGrantPermission.setOnClickListener {
            AlertDialog.Builder(this@ApplicationDetailActivity)
                .setTitle("确认要授权 ${model.name} 申请的权限么？")
                .setPositiveButton("确认") { dialog, _ ->
                    val grant = SystemPermissionCompat.grantRuntimePermission(packageName)

                    Toast.makeText(
                        this@ApplicationDetailActivity,
                        "授予：$grant",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}