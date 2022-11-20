package com.tomtruyen.soteria.android.ui.screens

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.viewinterop.AndroidView
import com.tomtruyen.soteria.android.R
import com.tomtruyen.soteria.android.extensions.collectAsStateLifecycleAware
import com.tomtruyen.soteria.android.ui.components.EmptyTokensMessage
import com.tomtruyen.soteria.android.ui.components.TokenItem
import com.tomtruyen.soteria.android.utils.DialogUtils
import com.tomtruyen.soteria.common.data.entities.Token
import com.tomtruyen.soteria.common.extensions.generateTOTP
import com.tomtruyen.widgets.efab.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenScreen(
    mViewModel: TokenViewModel = TokenViewModel(),
    navigateToAddTokenScreen: () -> Unit,
    navigateToScanTokenScreen: () -> Unit,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val tokens: List<Token> by mViewModel.tokens.collectAsStateLifecycleAware(initial = emptyList())
    val seconds: Int by mViewModel.seconds.collectAsStateLifecycleAware(initial = 0)

    var showActions by remember { mutableStateOf(false) }

    var toast: Toast? = null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if(!showActions) Text(text = stringResource(R.string.app_name))
                },
                navigationIcon = {
                    if(showActions) {
                        IconButton(onClick = { showActions = false }) {
                            Icon(Icons.Filled.ArrowBack, null)
                        }
                    }
                },
                actions = {
                    if(showActions) {
                        IconButton(onClick = {
                            DialogUtils.showEditDialog(context, mViewModel.selectedToken?.label ?: "") {
                                mViewModel.saveToken(mViewModel.selectedToken?.copy(label = it) ?: return@showEditDialog) {
                                    showActions = false
                                }
                            }
                        }) {
                            Icon(
                                Icons.Filled.Edit,
                                null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(onClick = {
                            DialogUtils.showDialog(
                                context = context,
                                title = context.getString(R.string.title_delete_token),
                                message = context.getString(R.string.message_delete_token, mViewModel.selectedToken?.label),
                                onCancel = {},
                                onConfirm = {
                                    mViewModel.deleteToken(mViewModel.selectedToken!!) {
                                        showActions = false
                                    }
                                }
                            )
                        }) {
                            Icon(
                                Icons.Filled.Delete,
                                null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context: Context ->
                    val view = LayoutInflater.from(context)
                        .inflate(R.layout.layout_expandable_fab, null, false)

                    view.findViewById<FabOption>(R.id.setupKeyButton).setOnClickListener {
                        navigateToAddTokenScreen.invoke()
                    }

                    view.findViewById<FabOption>(R.id.qrButton).setOnClickListener {
                        navigateToScanTokenScreen.invoke()
                    }

                    view
                },
                update = { }
            )
        }
    ) { innerPadding ->
        if(tokens.isEmpty()) {
            EmptyTokensMessage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    itemsIndexed(tokens) { _, token ->
                        TokenItem(
                            token = token,
                            seconds = seconds,
                            onPress = {
                                val code = token.generateTOTP()

                                clipboardManager.setText(AnnotatedString(code))

                                toast?.cancel()
                                toast = Toast.makeText(
                                    context,
                                    "Copied $code to clipboard",
                                    Toast.LENGTH_LONG
                                )
                                toast?.show()
                            },
                            onLongPress = {
                                mViewModel.selectedToken = token
                                showActions = true
                            }
                        )
                }
            }
        }
    }
}
