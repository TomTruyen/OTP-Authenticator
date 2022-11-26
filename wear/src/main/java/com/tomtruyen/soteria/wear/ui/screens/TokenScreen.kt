package com.tomtruyen.soteria.wear.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.*
import com.tomtruyen.soteria.common.data.entities.Token
import com.tomtruyen.soteria.wear.extensions.collectAsStateLifecycleAware
import com.tomtruyen.soteria.wear.ui.components.EmptyTokensMessage

@Composable
fun TokenScreen(
    mViewModel: TokenViewModel,
    onTokenClick: (String) -> Unit
) {
    val tokens: List<Token> by mViewModel.tokens.collectAsStateLifecycleAware(initial = emptyList())

    if(tokens.isEmpty()) {
        EmptyTokensMessage(modifier = Modifier.fillMaxSize())
    } else {
        ScalingLazyColumn {
            itemsIndexed(tokens) { _, token ->
                Chip(
                    onClick = { onTokenClick(token.id) },
                    label = { Text(text = token.label) },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
        }
    }
}