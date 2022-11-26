package com.tomtruyen.soteria.wear.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.tomtruyen.soteria.common.data.entities.Token
import com.tomtruyen.soteria.common.extensions.generateTOTP
import com.tomtruyen.soteria.wear.extensions.collectAsStateLifecycleAware
import com.tomtruyen.soteria.wear.ui.screens.detail.TokenDetailViewModel

@Composable
fun TokenDetailScreen(
    mViewModel: TokenDetailViewModel,
) {
    val token: Token? by mViewModel.token!!.collectAsStateLifecycleAware(initial = null)
    val seconds: Int by mViewModel.seconds.collectAsStateLifecycleAware(initial = 0)

    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            trackColor = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
            indicatorColor = MaterialTheme.colors.secondary,
            strokeWidth = 4.dp
        )

        Column {
            Text(text = token!!.label, style = MaterialTheme.typography.body2)
            Text(text = token!!.generateTOTP(), style = MaterialTheme.typography.title2)
        }
    }

}