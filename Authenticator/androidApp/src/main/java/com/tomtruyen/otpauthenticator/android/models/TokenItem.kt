package com.tomtruyen.otpauthenticator.android.models

class TokenItem(private val title: String, private val token: String) {
    fun toHashMap() : Map<String, String> {
        return hashMapOf("title" to title, "token" to token)
    }
}