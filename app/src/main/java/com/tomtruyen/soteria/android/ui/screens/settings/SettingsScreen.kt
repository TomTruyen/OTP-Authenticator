package com.tomtruyen.soteria.android.ui.screens.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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