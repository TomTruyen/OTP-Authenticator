package com.tomtruyen.soteria.wear.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.tomtruyen.soteria.common.data.entities.Token
import com.tomtruyen.soteria.common.extensions.generateTOTP
import com.tomtruyen.soteria.common.extensions.getIssuer
import com.tomtruyen.soteria.common.extensions.getUsername
import com.tomtruyen.soteria.wear.extensions.collectAsStateLifecycleAware
import com.tomtruyen.soteria.wear.ui.screens.detail.TokenDetailViewModel

@Composable
fun TokenDetailScreen(
    mViewModel: TokenDetailViewModel,
) {
    val token: Token? by mViewModel.token.collectAsStateLifecycleAware(initial = null)
    val seconds: Int by mViewModel.seconds.collectAsStateLifecycleAware(initial = 0)

    val currentPercentage = remember { Animatable(0f) }

    LaunchedEffect(seconds.toFloat() / 30f) {
        currentPercentage.animateTo(
            seconds.toFloat() / 30f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }

    if(token == null) {
        CircularProgressIndicator()
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                trackColor = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
                indicatorColor = MaterialTheme.colors.primary,
                strokeWidth = 4.dp,
                progress = currentPercentage.value,
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (token!!.label.contains("(") && token!!.label.contains(")")) {
                    Text(text = token!!.getIssuer(), style = MaterialTheme.typography.body2)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = token!!.getUsername(), style = MaterialTheme.typography.caption3.copy(
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                    ))
                } else {
                    Text(text = token!!.label, style = MaterialTheme.typography.body2)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = token!!.generateTOTP(), style = MaterialTheme.typography.title1)
            }
        }
    }
}