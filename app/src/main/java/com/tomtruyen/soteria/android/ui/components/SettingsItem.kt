package com.tomtruyen.soteria.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tomtruyen.soteria.android.models.Setting

@Composable
fun SettingsItem(item: Setting) {
    Column(
        modifier = Modifier
            .clickable(onClick = item.onClick)
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = item.title)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.subtitle,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
    }
}