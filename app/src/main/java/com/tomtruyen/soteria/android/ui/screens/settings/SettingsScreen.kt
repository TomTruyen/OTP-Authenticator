package com.tomtruyen.soteria.android.ui.screens.settings

import android.app.Activity
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
import com.tomtruyen.soteria.android.R
import com.tomtruyen.soteria.android.models.Setting
import com.tomtruyen.soteria.android.ui.components.SettingsItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    mViewModel: SettingsViewModel = SettingsViewModel(),
    navigateUp: () -> Unit
) {
    val context = LocalContext.current
    val settings = listOf(
        Setting(
            title = stringResource(R.string.title_import),
            subtitle = stringResource(R.string.subtitle_import),
            onClick = { }
        ),
        Setting(
            title = stringResource(R.string.title_export),
            subtitle = stringResource(R.string.subtitle_export),
            onClick = { }
        ),
        Setting(
            title = stringResource(R.string.title_export_drive),
            subtitle = stringResource(R.string.subtitle_export_drive),
            onClick = { }
        ),
    )

    val mExportDriveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, context.getString(R.string.exporting), Toast.LENGTH_SHORT).show()
            mViewModel.handleGoogleExport(
                context,
                result.data,
                onSuccess = {
                    Toast.makeText(context, context.getString(R.string.exported), Toast.LENGTH_SHORT).show()
                },
                onFailure = {
                    Toast.makeText(context, context.getString(R.string.error_drive_upload), Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

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