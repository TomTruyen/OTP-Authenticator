package com.tomtruyen.soteria.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Typography

private val darkColors = Colors(
    primary = Purple80,
    secondary = PurpleGrey80,
)

private val lightColors = Colors(
    primary = Purple40,
    secondary = PurpleGrey40,
)

@Composable
fun SoteriaWearTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when {
        darkTheme -> darkColors
        else -> lightColors
    }

    MaterialTheme(
        colors = colors,
        typography = Typography(),
        content = content
    )
}