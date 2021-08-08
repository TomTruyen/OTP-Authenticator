package com.tomtruyen.soteria.android.models.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tomtruyen.soteria.android.R
import com.tomtruyen.soteria.android.models.DatabaseService
import com.tomtruyen.soteria.android.models.Divider

class SettingsAdapter(private val context: Context) : BaseAdapter() {
    private var mSettingsList = mutableListOf<Any>()

    init {
        val dataDivider = Divider("Data")
        val importSetting = Setting("Import", "Import an exported file (backup)")
        val exportSetting = Setting("Export", "Export your accounts to a file (backup)")
        val driveSetting = Setting("Export to Drive", "Export your accounts to Google Drive")

        val securityDivider = Divider("Security")
        val db = DatabaseService(context)
        val pinEnabled = db.isPinEnabled()

        var pinTitle = "Enable PIN"
        var subTitle = "Set a 5 digits PIN to secure your authenticator"
        if (pinEnabled) {
            pinTitle = "Remove PIN"
            subTitle = "Remove the PIN from the authenticator"
        }

        val pinSetting = Setting(pinTitle, subTitle)

        val endDivider = Divider()

        mSettingsList.add(dataDivider)
        mSettingsList.add(importSetting)
        mSettingsList.add(exportSetting)
        mSettingsList.add(driveSetting)

        mSettingsList.add(securityDivider)
        mSettingsList.add(pinSetting)

        mSettingsList.add(endDivider)
    }

    override fun getCount(): Int {
        return mSettingsList.size
    }

    override fun getItem(position: Int): Any {
        return mSettingsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var item = getItem(position)

        val v: View = if (convertView == null) {
            val inflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            if(item is Divider) {
                inflater.inflate(R.layout.settings_divider_item, parent, false)
            } else {
                inflater.inflate(R.layout.settings_item, parent, false)
            }
        } else {
            convertView
        }

        val title: TextView = v.findViewById(R.id.settingTitle)
        if(item is Divider) {
            v.isEnabled = false
            v.setOnClickListener(null)

            title.text = item.title

            if(position == 0) v.findViewById<TextView>(R.id.divider).visibility = View.GONE
        } else {
            item = item as Setting
            title.text = item.title
            val subtitle: TextView = v.findViewById(R.id.settingSubtitle)
            subtitle.text = item.subtitle
        }

        return v
    }
}