package com.tomtruyen.soteria.android

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.tomtruyen.soteria.android.databinding.ActivitySettingsBinding
import com.tomtruyen.soteria.android.models.settings.SettingsAdapter
import com.tomtruyen.soteria.android.models.DatabaseService
import com.tomtruyen.soteria.android.services.DriveService
import com.tomtruyen.soteria.android.utils.Utils
import java.util.*


class SettingsActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySettingsBinding
    private lateinit var mSettingsAdapter: SettingsAdapter
    private lateinit var mDatabaseService: DatabaseService
    private lateinit var mUtils: Utils
    private lateinit var mDriveService: DriveService
    private lateinit var mExportDriveStartForResult : ActivityResultLauncher<Intent>
    private lateinit var mImportStartForResult : ActivityResultLauncher<Intent>

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 999
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivitySettingsBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        // Toolbar Setup
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Settings"
        setSupportActionBar(toolbar)

        // TokenPersistence Init
        mDatabaseService = DatabaseService(this)

        // Utils init
        mUtils = Utils()

        // DriveService init
        mDriveService = DriveService(this, this, mDatabaseService)

        // SettingAdapter
        mSettingsAdapter = SettingsAdapter(this)
        mBinding.settingsList.adapter = mSettingsAdapter

        // StartActivityResult Launchers
        mExportDriveStartForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                handleGoogleExport(intent)
            }
        }

        mImportStartForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK) {
                val resultIntent = result.data
                handleFileImport(resultIntent)
            }
        }

        // Listview
        val listview = findViewById<ListView>(R.id.settingsList)

        listview.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _ ->
            val setting = mSettingsAdapter.getItem(position)

            when (setting.title.lowercase()) {
                "import" -> openFilePicker()
                "export" -> {
                    if (hasWriteStoragePermission()) {
                        val path = mDatabaseService.exportToken()

                        if (path == null) {
                            Toast.makeText(this, "Failed to create backup", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this, "Backup created at $path", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                "export to drive" -> {
                    if (hasWriteStoragePermission()) {
                        mExportDriveStartForResult.launch(mDriveService.mClient.signInIntent)
                    }
                }
                "enable passcode" -> {
                    val intent = Intent(this, LockScreenActivity::class.java)
                    intent.putExtra("isEnable", true)
                    startActivity(intent)
                }
            }
        }
    }

    private fun handleGoogleExport(data: Intent?) {
        mDriveService.handleSignInIntent(data).addOnSuccessListener {
            val credential = GoogleAccountCredential.usingOAuth2(
                this@SettingsActivity, Collections.singleton(
                    DriveScopes.DRIVE_FILE
                )
            )
            credential.selectedAccount = it.account

            mDriveService.mDrive = Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory(),
                credential
            )
                .setApplicationName(
                    this@SettingsActivity.resources.getString(R.string.app_name)
                )
                .build()

            val filePath = mDatabaseService.exportToken()
            if (filePath != null) {
                val toast = Toast.makeText(
                    this@SettingsActivity,
                    "Uploading to Drive...",
                    Toast.LENGTH_SHORT
                )

                toast.show()

                mDriveService.upload(filePath, toast)
            }

        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    private fun handleFileImport(data: Intent?) {
        try {
            if (data == null) return

            val uri = data.data ?: return

            if (uri.path == null) return

            val file = mUtils.getFileFromUri(contentResolver, uri, cacheDir)

            if (mDatabaseService.importToken(file)) {
                Toast.makeText(this, "Backup restored", Toast.LENGTH_SHORT).show()
            } else {
                throw Exception()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to restore backup", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFilePicker() {
        if (hasWriteStoragePermission()) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"

            mImportStartForResult.launch(intent)
        }
    }

    private fun hasWriteStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )

                return false
            }
        }

        return true
    }

}