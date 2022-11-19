package com.tomtruyen.soteria.android.ui.screens

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nambimobile.widgets.efab.FabOption
import com.tomtruyen.soteria.android.R
import com.tomtruyen.soteria.android.extensions.collectAsStateLifecycleAware
import com.tomtruyen.soteria.android.ui.components.TokenItem
import com.tomtruyen.soteria.common.data.entities.Token
import com.tomtruyen.soteria.common.extensions.generateTOTP

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

    var toast: Toast? = null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
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
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            itemsIndexed(tokens) { _, token ->
                TokenItem(
                    token = token,
                    seconds = seconds,
                    onPress = {
                        val code = token.generateTOTP()

                        clipboardManager.setText(AnnotatedString(code))

                        toast?.cancel()
                        toast = Toast.makeText(context, "Copied $code to clipboard", Toast.LENGTH_LONG)
                        toast?.show()
                    },
                    onLongPress = {
                        // TODO: Show options to edit/delete (do it in some kind of menu instead of changing the appbar)
                    }
                )
            }
        }
    }
}
