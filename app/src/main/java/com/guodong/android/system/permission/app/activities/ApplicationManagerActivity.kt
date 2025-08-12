package com.guodong.android.system.permission.app.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.guodong.android.system.permission.SystemPermissionCompat
import com.guodong.android.system.permission.app.BaseActivity
import com.guodong.android.system.permission.app.R
import com.guodong.android.system.permission.app.adapter.ApplicationAdapter
import com.guodong.android.system.permission.app.databinding.ActivityApplicationManagerBinding
import com.guodong.android.system.permission.app.getApplicationModels
import com.guodong.android.system.permission.app.model.ApplicationModel
import kotlinx.coroutines.launch
import me.rosuh.filepicker.config.FilePickerManager

/**
 * Created by guodongAndroid on 2025/8/15
 */
class ApplicationManagerActivity : BaseActivity<ActivityApplicationManagerBinding>(),
    OnRefreshListener {

    companion object {
        private const val TAG = "ApplicationManagerActivity"

        fun start(context: Context) {
            context.startActivity(Intent(context, ApplicationManagerActivity::class.java))
        }
    }

    private val modes = mutableListOf<ApplicationModel>()
    private val adapter = ApplicationAdapter(modes)

    override fun getViewBinding(): ActivityApplicationManagerBinding {
        return ActivityApplicationManagerBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivityApplicationManagerBinding.initViews() {
        btnChooseApk.setOnClickListener {
            FilePickerManager.from(this@ApplicationManagerActivity)
                .enableSingleChoice()
                .forResult(FilePickerManager.REQUEST_CODE)
            it.isEnabled = false
        }

        srLayout.setOnRefreshListener(this@ApplicationManagerActivity)

        adapter.setOnApplicationItemClickListener {
            Toast.makeText(this@ApplicationManagerActivity, it.name, Toast.LENGTH_SHORT).show()
            ApplicationDetailActivity.start(this@ApplicationManagerActivity, it.packageName)
        }

        rvApp.adapter = adapter
        rvApp.addItemDecoration(
            DividerItemDecoration(
                this@ApplicationManagerActivity,
                DividerItemDecoration.VERTICAL
            )
        )
        rvApp.itemAnimator = null
        rvApp.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()
        binding.refreshUI()
    }

    @Deprecated("")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FilePickerManager.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val files = FilePickerManager.obtainData()
                val apkFilePath = files.first()
                if (!apkFilePath.endsWith(".apk")) {
                    Toast.makeText(this, "Please choose a apk file", Toast.LENGTH_SHORT).show()
                    binding.btnChooseApk.isEnabled = true
                    return
                }

                Toast.makeText(this, "Choose apk file path: $apkFilePath", Toast.LENGTH_SHORT)
                    .show()
                binding.btnChooseApk.text = getString(R.string.installing)
                SystemPermissionCompat.installPackage(apkFilePath) { packageName, isSuccessful, status, message, _ ->
                    Log.d(
                        TAG,
                        "installPackage: $packageName, $isSuccessful, $status, $message"
                    )
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Install $packageName $isSuccessful",
                            Toast.LENGTH_SHORT
                        ).show()

                        binding.refreshUI()
                        binding.btnChooseApk.isEnabled = true
                        binding.btnChooseApk.text = getString(R.string.choose_apk_file)
                    }
                }
            } else {
                binding.btnChooseApk.isEnabled = true
                Toast.makeText(this, "You didn't choose anything~", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ActivityApplicationManagerBinding.refreshUI() {
        lifecycleScope.launch {
            srLayout.isRefreshing = true

            val applications = getApplicationModels()
            val size = modes.size
            adapter.notifyItemRangeRemoved(0, size)

            modes.clear()
            modes.addAll(applications)
            adapter.notifyItemRangeInserted(0, modes.size)

            srLayout.isRefreshing = false
        }
    }

    override fun onRefresh() {
        binding.refreshUI()
    }
}