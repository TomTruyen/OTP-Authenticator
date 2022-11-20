package com.tomtruyen.soteria.android.ui.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tomtruyen.soteria.android.R
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navigateUp: () -> Unit) {
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
    ) {
    }
}