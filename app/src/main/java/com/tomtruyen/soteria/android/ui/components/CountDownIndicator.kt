package com.tomtruyen.soteria.android.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CountDownIndicator(
    progress: Float,
    color: Color = MaterialTheme.colorScheme.primary,
    seconds: Int
) {
    val currentPercentage = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        currentPercentage.animateTo(
            progress,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Background arc
            drawArc(
                color = Color(0xFFF0F0F0),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(3.dp.toPx(), cap = StrokeCap.Butt)
            )

            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = -360f * currentPercentage.value,
                useCenter = false,
                style = Stroke(3.dp.toPx(), cap = StrokeCap.Butt)
            )
        }

        Text(
            // +1 so that it never shows "0" as seconds remaining
            text = seconds.toString(),
            fontSize = 16.sp,
        )
    }

}