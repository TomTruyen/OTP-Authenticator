package com.tomtruyen.soteria.android.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomtruyen.soteria.common.data.entities.Token
import com.tomtruyen.soteria.android.repositories.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.concurrent.fixedRateTimer

class TokenViewModel: ViewModel() {
    val tokens = TokenRepository.tokenDao.findAllLive()
}