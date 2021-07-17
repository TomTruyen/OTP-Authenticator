package com.tomtruyen.otpauthenticator.android

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
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

        // TokenPersistence Init
        mTokenPersistence = TokenPersistence(this)

        // SettingAdapter
        mSettingsAdapter = SettingsAdapter(this)
        mBinding.settingsList.adapter = mSettingsAdapter

        // Listview
        val listview = findViewById<ListView>(R.id.settingsList)

        listview.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _ ->
            val setting = mSettingsAdapter.getItem(position)

            when(setting.title.lowercase()) {
                "import" -> openFilePicker()
                "export" -> {
                    if(hasWriteStoragePermission()) {
                        val path = mTokenPersistence.export()

                        if (path == null) {
                            Toast.makeText(this, "Failed to create backup", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this, "Backup created at $path", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 999 = Import Backup File Received
        // 998 = Permission (storage) requested
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                999 -> {
                    if(data == null) return

                    val uri = data.data ?: return

                    if(uri.path == null) return

                    val filename = getFileName(uri) ?: return

                    val file = File(mTokenPersistence.path, filename)

                    if(mTokenPersistence.import(file)) {
                        Toast.makeText(this, "Backup restored", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to restore backup", Toast.LENGTH_SHORT).show()
                    }
                }
                998 -> {

                }
            }
        }
    }

    private fun openFilePicker() {
        if(hasWriteStoragePermission()) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, 999)
        }
    }

    private fun hasWriteStoragePermission() : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    998
                )

                return false
            }
        }

        return true
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            cursor.use { c ->
                if (c != null && c.moveToFirst()) {
                    result = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }


}