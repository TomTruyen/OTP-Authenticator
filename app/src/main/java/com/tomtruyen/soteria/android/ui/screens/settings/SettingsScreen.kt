package com.tomtruyen.soteria.android.ui.screens.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tomtruyen.soteria.android.R
import com.tomtruyen.soteria.android.models.Setting
import com.tomtruyen.soteria.android.ui.components.SettingsItem
import org.koin.androidx.compose.get
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    mViewModel: SettingsViewModel,
    navigateUp: () -> Unit
) {
    val context = LocalContext.current

    val mExportDriveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Toast.makeText(context, context.getString(R.string.exporting), Toast.LENGTH_SHORT).show()
        mViewModel.handleGoogleExport(
            result.data,
            onSuccess = {
                Toast.makeText(context, context.getString(R.string.exported_drive), Toast.LENGTH_SHORT).show()
            },
            onFailure = {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_drive_upload),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    val mImportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val file = File.createTempFile("suffix", "prefix")

                file.outputStream().use {
                    context.contentResolver.openInputStream(uri)?.copyTo(it)
                }

                mViewModel.import(
                    file = file,
                    onSuccess = {
                        Toast.makeText(context, context.getString(R.string.imported), Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(context, context.getString(R.string.error_import), Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    val settings = listOf(
        Setting(
            title = stringResource(R.string.title_import),
            subtitle = stringResource(R.string.subtitle_import),
            onClick = {
                mImportLauncher.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "*/*"
                })
            }
        ),
        Setting(
            title = stringResource(R.string.title_export),
            subtitle = stringResource(R.string.subtitle_export),
            onClick = {
                mViewModel.export(
                    onSuccess = { path -> Toast.makeText(context, context.getString(R.string.exported, path), Toast.LENGTH_LONG).show() },
                    onFailure = { Toast.makeText(context, context.getString(R.string.error_export), Toast.LENGTH_LONG).show() }
                )
            }
        ),
        Setting(
            title = stringResource(R.string.title_export_drive),
            subtitle = stringResource(R.string.subtitle_export_drive),
            onClick = {
                mExportDriveLauncher.launch(mViewModel.mDriveService.mClient.signInIntent)
            }
        ),
        Setting(
            title = stringResource(R.string.sync_to_watch),
            subtitle = stringResource(R.string.sync_to_watch_subtitle),
            onClick = {
                mViewModel.syncToWatch(context) {
                    Toast.makeText(context, context.getString(R.string.synced_to_watch), Toast.LENGTH_SHORT).show()
                }
            }
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.title_settings))
                },
                navigationIcon = {
                    IconButton(onClick = { navigateUp.invoke() }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            itemsIndexed(settings) { _, item ->
                SettingsItem(item = item)
            }
        }
    }
}