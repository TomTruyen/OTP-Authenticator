package com.tomtruyen.soteria.android.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomtruyen.soteria.common.data.entities.Token
import com.tomtruyen.soteria.common.extensions.generateTOTP
import kotlin.math.round

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TokenItem(
    token: Token,
    seconds: Int,
    onPress: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .combinedClickable(
                onClick = onPress,
                onLongClick = onLongPress
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = token.label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = token.generateTOTP(),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                CountDownIndicator(
                    progress = seconds / 30f,
                    seconds = seconds,
                )
            }
        }

        Divider()
    }
}