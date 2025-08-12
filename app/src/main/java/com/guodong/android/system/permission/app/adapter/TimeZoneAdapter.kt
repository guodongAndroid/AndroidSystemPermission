package com.guodong.android.system.permission.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.guodong.android.system.permission.app.R
import com.guodong.android.system.permission.app.model.TimeZoneModel

/**
 * Created by guodongAndroid on 2025/8/14
 */
class TimeZoneAdapter(
    private val context: Context,
    private val zones: List<TimeZoneModel>,
) : ArrayAdapter<TimeZoneModel>(context, 0, zones) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return viewHolder(convertView, parent, position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return viewHolder(convertView, parent, position)
    }

    @SuppressLint("SetTextI18n")
    private fun viewHolder(
        convertView: View?,
        parent: ViewGroup,
        position: Int
    ): View {
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(context)
                .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        }

        val tv = view!!.findViewById<TextView>(android.R.id.text1)
        val model = zones[position]
        tv.text = "${position + 1}. $model"
        if (model.isSelected) {
            view.setBackgroundColor(Color.YELLOW)
        } else {
            view.setBackgroundColor(context.getColor(R.color.item_bg_color))
        }

        return view
    }
}