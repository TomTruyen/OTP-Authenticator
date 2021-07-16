package com.tomtruyen.otpauthenticator.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.tomtruyen.otpauthenticator.android.databinding.ActivityAddTokenSetupKeyBinding
import com.tomtruyen.otpauthenticator.android.databinding.ActivitySettingsBinding
import com.tomtruyen.otpauthenticator.android.models.settings.SettingsAdapter
import com.tomtruyen.otpauthenticator.android.models.token.TokenPersistence
import java.io.File

class SettingsActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivitySettingsBinding
    private lateinit var mSettingsAdapter : SettingsAdapter
    private lateinit var mTokenPersistence : TokenPersistence

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivitySettingsBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        // Toolbar Setup
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Settings"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Listview
        val listview = findViewById<ListView>(R.id.settingsList)

        listview.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _ ->
            val setting = mSettingsAdapter.getItem(position)

            when(setting.title.lowercase()) {
                "import" -> println("READ FILE + IMPORT")
                "export" -> println("EXPORT")
            }
        }

        // SettingAdapter
        mSettingsAdapter = SettingsAdapter(this)
        mBinding.settingsList.adapter = mSettingsAdapter

        // TokenPersistence Init
        mTokenPersistence = TokenPersistence(this)
    }


}