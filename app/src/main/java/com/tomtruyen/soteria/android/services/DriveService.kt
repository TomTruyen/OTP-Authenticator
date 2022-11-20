package com.tomtruyen.soteria.android.services

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.tomtruyen.soteria.android.App
import com.google.api.services.drive.model.File as DriveFile
import com.tomtruyen.soteria.common.data.entities.DriveFile as DriveFileEntity
import com.tomtruyen.soteria.android.R
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class DriveService(private val context: Context) {
    private val filePath = context.getExternalFilesDir(null)?.absolutePath + "/SoteriaBackup-${System.currentTimeMillis()}"

    private var mClient: GoogleSignInClient
    lateinit var mDrive: Drive

    init {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()

        mClient = GoogleSignIn.getClient(context, signInOptions)
    }

    fun handleSignInIntent(data: Intent?, onSuccess: () -> Unit, onFailure: () -> Unit) {
        GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener {
            val credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_FILE)).apply {
                selectedAccount = it.result?.account
            }

            mDrive = Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory(),
                credential
            ).setApplicationName(context.getString(R.string.app_name)).build()

            CoroutineScope(Dispatchers.IO).launch {
                upload(onSuccess, onFailure)
            }
        }.addOnFailureListener {
            onFailure.invoke()
        }
    }

    private suspend fun upload(onSuccess: () -> Unit, onFailure: () -> Unit) = withContext(Dispatchers.IO) {
        try {
            val driveFile = DriveFile().apply {
                originalFilename = "${context.getString(R.string.app_name)}-Backup"
                name = "${context.getString(R.string.app_name)}-Backup"
            }

            val localFile = File(filePath)
            val localFileContent = FileContent("application/json", localFile)

            App.database.driveDao().findFirst()?.let {
                try {
                    mDrive.files().delete(it.id).execute()
                } catch (_: Exception) {
                }
            }

            mDrive.files().create(driveFile, localFileContent).execute()?.also {
                App.database.driveDao().insertManyDeleteOthers(
                    listOf(DriveFileEntity(it.id))
                )
            }

            localFile.delete()

            onSuccess.invoke()
        } catch (e: Exception) {
            onFailure.invoke()
        }
    }
}