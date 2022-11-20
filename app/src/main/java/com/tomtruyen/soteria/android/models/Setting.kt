package com.tomtruyen.soteria.android.models

data class Setting(
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)