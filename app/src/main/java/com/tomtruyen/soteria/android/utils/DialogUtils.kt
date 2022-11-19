package com.tomtruyen.soteria.android.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtils {
    fun showErrorDialog(context: Context, title: String = "Error", message: String, onConfirm: (() -> Unit)? = null, onDismissListener: (() -> Unit)? = null): AlertDialog? {
        return MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setOnDismissListener { onDismissListener?.invoke() }
            .setPositiveButton("OK") { dialog, _ ->
                onConfirm?.invoke()
                dialog.dismiss()
            }
            .show()
    }
}