package com.tomtruyen.soteria.wear.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.tomtruyen.soteria.common.data.entities.Token
import com.tomtruyen.soteria.common.extensions.getIssuer
import com.tomtruyen.soteria.common.extensions.getUsername
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
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onTokenClick(token.id) },
                    label = {
                        if (token.label.contains("(") && token.label.contains(")")) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(text = token.getIssuer(), style = MaterialTheme.typography.body2)
                                Text(text = token.getUsername(), style = MaterialTheme.typography.caption3.copy(
                                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                                ))
                            }
                        } else {
                            Text(text = token.label, style = MaterialTheme.typography.body2)
                        }
                    },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
        }
    }
}