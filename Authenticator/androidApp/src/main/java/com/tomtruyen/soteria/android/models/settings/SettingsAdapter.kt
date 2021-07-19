package com.tomtruyen.soteria.android.models.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tomtruyen.soteria.android.R

class SettingsAdapter(private val context: Context) : BaseAdapter() {
    private val mSettingsList = mutableListOf<Setting>()

    init {
        val importSetting = Setting("Import", "Import an exported file (backup)")
        val exportSetting = Setting("Export", "Export your accounts to a file (backup)")
        val driveSetting = Setting("Export to Drive", "Export your accounts to Google Drive")

        mSettingsList.add(importSetting)
        mSettingsList.add(exportSetting)
        mSettingsList.add(driveSetting)
    }

    override fun getCount(): Int {
        return mSettingsList.size
    }

    override fun getItem(position: Int): Setting {
        return mSettingsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View = if (convertView == null) {
            val inflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.settings_item, parent, false)
        } else {
            convertView
        }

        val s: Setting = getItem(position)

        val title: TextView = v.findViewById(R.id.settingTitle)
        val subtitle: TextView = v.findViewById(R.id.settingSubtitle)
        title.text = s.title
        subtitle.text = s.subtitle

        return v
    }
}