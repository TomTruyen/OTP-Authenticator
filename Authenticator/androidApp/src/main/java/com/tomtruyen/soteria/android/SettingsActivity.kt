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
import com.tomtruyen.soteria.android.models.token.TokenPersistence
import com.tomtruyen.soteria.android.services.DriveService
import com.tomtruyen.soteria.android.utils.Utils
import java.io.File
import java.util.*


class SettingsActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySettingsBinding
    private lateinit var mSettingsAdapter: SettingsAdapter
    private lateinit var mTokenPersistence: TokenPersistence
    private lateinit var mUtils: Utils
    private lateinit var mDriveService: DriveService

    companion object {
        private const val REQUEST_CODE_FILE = 999
        private const val REQUEST_CODE_STORAGE_PERMISSION = 998
        private const val REQUEST_CODE_GOOGLE_SIGN_IN = 997
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
        mTokenPersistence = TokenPersistence(this)

        // Utils init
        mUtils = Utils(this)

        // DriveService init
        mDriveService = DriveService(this)

        // SettingAdapter
        mSettingsAdapter = SettingsAdapter(this)
        mBinding.settingsList.adapter = mSettingsAdapter

        // Listview
        val listview = findViewById<ListView>(R.id.settingsList)

        listview.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _ ->
            val setting = mSettingsAdapter.getItem(position)

            when (setting.title.lowercase()) {
                "import" -> openFilePicker()
                "export" -> {
                    if (hasWriteStoragePermission()) {
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
                "export to drive" -> {
                    if (hasWriteStoragePermission()) {
                        startActivityForResult(
                            mDriveService.mClient.signInIntent,
                            REQUEST_CODE_GOOGLE_SIGN_IN
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_FILE -> {
                    if (data == null) return

                    val uri = data.data ?: return

                    if (uri.path == null) return

                    val filename = mUtils.getFileNameFromURI(uri) ?: return

                    val file = File(TokenPersistence.getPath(this), filename)

                    if (mTokenPersistence.import(file)) {
                        Toast.makeText(this, "Backup restored", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to restore backup", Toast.LENGTH_SHORT).show()
                    }
                }
                REQUEST_CODE_GOOGLE_SIGN_IN -> {
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
                                    .toString()
                            )
                            .build()

                        val filePath = mTokenPersistence.export()

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
                        println("Sign In Error: ${it.message}")
                        it.printStackTrace()
                    }
                }
            }
        }
    }

    private fun openFilePicker() {
        if (hasWriteStoragePermission()) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, REQUEST_CODE_FILE)
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