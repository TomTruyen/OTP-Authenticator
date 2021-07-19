package com.tomtruyen.soteria.android.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.tomtruyen.soteria.android.R
import com.tomtruyen.soteria.android.models.token.TokenPersistence
import java.io.File
import java.io.IOException
import com.google.api.services.drive.model.File as DriveFile


class DriveService(private val context: Context, private val tokenPersistence: TokenPersistence) {
    var mClient: GoogleSignInClient
    lateinit var mDrive: Drive


    init {
        val signInOptions: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                .build()

        mClient = GoogleSignIn.getClient(context, signInOptions)
    }

    fun handleSignInIntent(data: Intent?): Task<GoogleSignInAccount> {
        return GoogleSignIn.getSignedInAccountFromIntent(data)
    }

    fun upload(filePath: String, activeToast: Toast) {
        Thread {
            val activity = context as Activity

            try {
                val fileMetaData = DriveFile()
                fileMetaData.originalFilename = "${context.resources.getString(R.string.app_name)}-Backup"
                fileMetaData.name = "${context.resources.getString(R.string.app_name)}-Backup"

                val file = File(filePath)

                val content = FileContent("application/json", file)

                val driveFileId = tokenPersistence.readDriveFileId()

                if(driveFileId != null) {
                    mDrive.files().delete(driveFileId).execute()
                }

                val driveFile = mDrive.files().create(fileMetaData, content).execute() ?: throw Exception()

                // Delete tempFile
                file.delete()

                activity.runOnUiThread {
                    tokenPersistence.setDriveFileId(driveFile.id)
                    activeToast.cancel()
                    Toast.makeText(context, "Upload complete", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                activity.runOnUiThread {
                    activeToast.cancel()
                    Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()


    }

}