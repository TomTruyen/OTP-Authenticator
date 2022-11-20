package com.tomtruyen.soteria.android.ui.screens.settings

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.tomtruyen.soteria.android.services.DriveService

class SettingsViewModel: ViewModel() {
    fun handleGoogleExport(
        context: Context,
        data: Intent?,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        DriveService(context).also {
            it.handleSignInIntent(
                data = data,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }
}