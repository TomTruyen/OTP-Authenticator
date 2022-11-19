package com.tomtruyen.soteria.common.utils

import android.net.Uri

object OTPUtils {
    fun isUriValid(uri: Uri): Boolean {
        if(uri.scheme != "otpauth") return false

        if(uri.authority == null) return false

        if(uri.path == null) return false

        if(uri.getQueryParameter("secret") == null) return false

        return true
    }

    fun getLabelFromUri(uri: Uri, path: String): String {
        val index = path.indexOf(":")

        val issuerInt = uri.getQueryParameter("issuer") ?: ""
        val issuerExt = if(index < 0) "" else path.substring(0, index)

        val username = path.substring(if(index >= 0) index + 1 else 0)

        return when {
            issuerInt.isNotEmpty() -> "$issuerInt ($username)"
            issuerExt.isNotEmpty() -> "$issuerExt ($username)"
            else -> username
        }
    }

    fun getAlgorithmFromUri(uri: Uri): String {
        return uri.getQueryParameter("algorithm")?.uppercase() ?: "SHA1"
    }

    fun getDigitsFromUri(uri: Uri): Int {
        return try {
            (uri.getQueryParameter("digits") ?: "6").toInt()
        } catch (e: NumberFormatException) {
            6
        }
    }

    fun getPeriodFromUri(uri: Uri): Long {
        return try {
            (uri.getQueryParameter("period") ?: "30").toLong()
        } catch (e: NumberFormatException) {
            30
        }
    }

    fun getSecretFromUri(uri: Uri): String {
        return uri.getQueryParameter("secret") ?: ""
    }

    fun getImageFromUri(uri: Uri): String? {
        return uri.getQueryParameter("image")
    }
}