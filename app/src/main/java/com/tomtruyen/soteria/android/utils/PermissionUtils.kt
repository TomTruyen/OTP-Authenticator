package com.tomtruyen.soteria.android.utils

import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

object PermissionUtils {
    const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA
    const val STORAGE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

    @Composable
    fun setupPermissionLauncher(onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}): ManagedActivityResultLauncher<String, Boolean> {
         return rememberLauncherForActivityResult(
             ActivityResultContracts.RequestPermission()
         ) { isGranted ->
             if(isGranted) {
                onSuccess()
             } else {
                onFailure()
             }
         }
    }

    fun hasPermission(context: Context, permission: String): Boolean {
        return context.checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}