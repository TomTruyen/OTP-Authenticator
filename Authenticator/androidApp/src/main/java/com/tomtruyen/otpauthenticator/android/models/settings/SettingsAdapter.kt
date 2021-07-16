package com.tomtruyen.otpauthenticator.android.models.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tomtruyen.otpauthenticator.android.R

class SettingsAdapter(private val context: Context) : BaseAdapter() {
    private val mSettingsList = mutableListOf<Setting>()
    private val mInflator: LayoutInflater = LayoutInflater.from(context)

    init {
        val exportSetting = Setting("Export", "Export your keys to a file (backup)")
        val importSetting = Setting("Import", "Import an exported file (backup)")

        mSettingsList.add(exportSetting)
        mSettingsList.add(importSetting)
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

        val s : Setting = getItem(position)

        val title: TextView = v.findViewById(R.id.settingTitle)
        val subtitle: TextView = v.findViewById(R.id.settingSubtitle)
        title.text = s.title
        subtitle.text = s.subtitle


        return v
    }
}