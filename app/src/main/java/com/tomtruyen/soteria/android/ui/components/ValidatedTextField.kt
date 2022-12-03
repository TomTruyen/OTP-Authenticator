package com.tomtruyen.soteria.android.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ValidatedTextField(label: String, value: TextFieldValue, isError: Boolean, onValueChange: (TextFieldValue) -> Unit, modifier: Modifier = Modifier) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = modifier,
        value = value,
        label = { Text(label) },
        onValueChange = { onValueChange(it) },
        singleLine = true,
        isError = isError,
        trailingIcon = {
            if(isError) {
                Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.error)
            }
        },
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        )
    )
}