package com.tomtruyen.soteria.android.ui.screens.add

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tomtruyen.soteria.android.R
import com.tomtruyen.soteria.android.ui.components.ValidatedTextField
import com.tomtruyen.soteria.android.ui.screens.TokenViewModel
import com.tomtruyen.soteria.common.data.entities.Token
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTokenScreen(
    mViewModel: TokenViewModel = TokenViewModel(),
    navigateUp: () -> Unit
) {
    var label by remember { mutableStateOf(TextFieldValue("")) }
    var secret by remember { mutableStateOf(TextFieldValue("")) }

    var labelError by rememberSaveable { mutableStateOf(false) }
    var secretError by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navigateUp.invoke() }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    labelError = label.text.isEmpty()
                    secretError = secret.text.isEmpty()

                    if(labelError || secretError) return@FloatingActionButton

                    mViewModel.addToken(
                        Token(
                            id = UUID.randomUUID().toString(),
                            label = label.text,
                            secret = secret.text,
                        )
                    ) {
                        navigateUp.invoke()
                    }
                }
            ) {
                Icon(Icons.Default.Check, null)
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ValidatedTextField(
                value = label,
                label = stringResource(R.string.title_label),
                onValueChange = { label = it },
                isError = labelError
            )
            Spacer(modifier = Modifier.height(8.dp))
            ValidatedTextField(
                value = secret,
                label = stringResource(R.string.title_secret),
                onValueChange = { secret = it },
                isError = secretError
            )
        }
    }
}