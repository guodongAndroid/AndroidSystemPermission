package com.guodong.android.system.permission.app.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.guodong.android.system.permission.app.databinding.ItemApplicationManagerBinding
import com.guodong.android.system.permission.app.model.ApplicationModel

/**
 * Created by guodongAndroid on 2025/8/15
 */
typealias OnApplicationItemClickListener = (model: ApplicationModel) -> Unit

class ApplicationAdapter(
    private val models: List<ApplicationModel>,
) : RecyclerView.Adapter<ApplicationAdapter.ViewHolder>() {

    private var onApplicationItemClickListener: OnApplicationItemClickListener? = null

    fun setOnApplicationItemClickListener(listener: OnApplicationItemClickListener) {
        onApplicationItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemApplicationManagerBinding.inflate(inflater, parent, false)
        val holder = ViewHolder(binding)

        binding.btnAction.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position < 0) {
                return@setOnClickListener
            }

            onApplicationItemClickListener?.invoke(models[position])
        }

        return holder
    }

    override fun getItemCount(): Int {
        return models.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(models[position])
    }

    class ViewHolder(
        private val binding: ItemApplicationManagerBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(model: ApplicationModel) {
            binding.ivAppIcon.setImageDrawable(model.icon)
            binding.tvAppNameVersion.text = "${model.name}/${model.version}"
            binding.tvAppPackageName.text = model.packageName
            binding.cardSystem.isVisible = model.isSystem
            binding.btnAction.isEnabled = !model.isSystem
        }
    }
}