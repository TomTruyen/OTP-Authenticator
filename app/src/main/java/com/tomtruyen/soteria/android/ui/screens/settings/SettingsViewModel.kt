package com.tomtruyen.soteria.android.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomtruyen.soteria.android.models.Setting
import com.tomtruyen.soteria.android.services.DriveService
import com.tomtruyen.soteria.android.services.FileService
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(val mDriveService: DriveService, val mFileService: FileService): ViewModel() {
    fun handleGoogleExport(
        data: Intent?,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        mDriveService.handleExport(
            data = data,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun export(onSuccess: (String) -> Unit, onFailure: () -> Unit) = viewModelScope.launch {
        val file = mFileService.export()

        if(file == null) onFailure() else onSuccess(file.absolutePath)
    }

    fun import(file: File, onSuccess: () -> Unit, onFailure: () -> Unit) = viewModelScope.launch {
        mFileService.import(
            file = file,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}