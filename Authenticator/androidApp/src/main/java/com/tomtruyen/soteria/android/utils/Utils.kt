package com.tomtruyen.soteria.android.utils

import android.content.ContentResolver
import android.net.Uri
import java.io.File


class Utils {
    fun getFileFromUri(contentResolver: ContentResolver, uri: Uri, directory: File): File {
        val file =
            File.createTempFile("suffix", "prefix", directory)
        file.outputStream().use {
            contentResolver.openInputStream(uri)?.copyTo(it)
        }

        return file
    }
}