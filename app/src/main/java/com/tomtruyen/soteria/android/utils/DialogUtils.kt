package com.tomtruyen.soteria.android.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tomtruyen.soteria.android.R
import com.tomtruyen.soteria.android.ui.screens.TokenViewModel

object DialogUtils {
    fun showDialog(context: Context, title: String = "Error", message: String, onConfirm: (() -> Unit)? = null, onCancel: (() -> Unit)? = null): AlertDialog? {
        val builder = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                onConfirm?.invoke()
                dialog.dismiss()
            }

        if(onCancel != null) {
            builder.setNegativeButton("Cancel") { dialog, _ ->
                onCancel.invoke()
                dialog.dismiss()
            }
        }

        return builder.show()
    }

    fun showEditDialog(context: Context, label: String, onConfirm: (String) -> Unit): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_token, null)

        val editTokenLayout = view.findViewById<TextInputLayout>(R.id.edit_token_layout)
        val editTokenInput = view.findViewById<TextInputEditText>(R.id.edit_token)
        editTokenInput.setText(label)

        return MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.title_edit_token))
            .setView(view)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("OK") { dialog, _ ->
                if(editTokenInput.text.toString().isEmpty()) {
                    editTokenLayout.error = context.getString(R.string.error_empty_token)
                    return@setPositiveButton
                }

                onConfirm.invoke(editTokenInput.text.toString())
                dialog.dismiss()
            }.show()
    }
}