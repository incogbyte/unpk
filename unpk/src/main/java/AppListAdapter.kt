package com.r0df.unpk.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.r0df.unpk.R
import com.r0df.unpk.utils.PackageHelper

class AppListAdapter(
    private val context: Context,
    private var apps: List<PackageHelper.AppInfo>,
    private val selectedApps: MutableSet<PackageHelper.AppInfo>
) : BaseAdapter() {


    fun updateList(filteredApps: List<PackageHelper.AppInfo>) {
        apps = filteredApps
        notifyDataSetChanged()
    }

    override fun getCount(): Int = apps.size

    override fun getItem(position: Int): PackageHelper.AppInfo = apps[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_app, parent, false)

        val appIcon: ImageView = view.findViewById(R.id.app_icon)
        val appLabel: TextView = view.findViewById(R.id.app_label)
        val appStatus: TextView = view.findViewById(R.id.app_status)
        val appCheckbox: CheckBox = view.findViewById(R.id.app_checkbox)

        val app = getItem(position)


        appIcon.setImageDrawable(app.icon)
        appLabel.text = app.label


        if (PackageHelper.isSplitAPK(context, app.packageName)) {
            appStatus.text = "Split APK"
            appStatus.setTextColor(Color.RED)
        } else {
            appStatus.text = "Not Split APK"
            appStatus.setTextColor(Color.parseColor("#006400"))
        }


        appCheckbox.isChecked = selectedApps.contains(app)
        appCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedApps.add(app)
            } else {
                selectedApps.remove(app)
            }
        }

        return view
    }
}