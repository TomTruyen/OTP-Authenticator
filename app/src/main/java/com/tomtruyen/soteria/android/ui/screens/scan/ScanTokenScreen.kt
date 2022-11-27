package com.tomtruyen.soteria.android.ui.screens.scan

import android.app.Activity
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.tomtruyen.soteria.android.ui.screens.TokenViewModel
import com.tomtruyen.soteria.common.data.entities.Token

@Composable
fun ScanTokenScreen(
    mViewModel: TokenViewModel,
    navigateUp: () -> Unit,
) {
    val context = LocalContext.current

    val compoundBarcodeView = remember {
        CompoundBarcodeView(context).apply {
            val capture = CaptureManager(context as Activity, this)
            capture.initializeFromIntent(context.intent, null)

            setStatusText("")
            resume()

            capture.decode()

            decodeSingle { result ->
                result.text?.let {
                    try {
                        Token.fromUri(Uri.parse(it)).let { token ->
                            mViewModel.saveToken(context, token) {
                                navigateUp.invoke()
                            }
                        }
                    } catch (e: Exception) { }
                }
            }
        }
    }

    AndroidView(factory = { compoundBarcodeView })
}