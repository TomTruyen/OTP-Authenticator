package com.tomtruyen.soteria.android.ui.screens

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.appwise.core.ui.base.BaseViewModel
import com.tomtruyen.soteria.android.repositories.TokenRepository
import com.tomtruyen.soteria.common.data.entities.Token
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.timerTask

class TokenViewModel: ViewModel() {
    val tokens = TokenRepository.tokenDao.findAllLive()

    private val _seconds = MutableStateFlow(30)
    val seconds = _seconds.asStateFlow()

    init {
        refreshProgress()
    }

    private fun refreshProgress() {
        fixedRateTimer(period = 1000) {
            val secondsUntilRefresh = 30 - (LocalDateTime.now().second % 30)
            _seconds.value = secondsUntilRefresh
        }
    }

    // can also be used to save the edited tokens
    fun saveToken(token: Token, onSuccess: () -> Unit) = viewModelScope.launch {
        TokenRepository.tokenDao.insert(token)
        onSuccess.invoke()
    }

    fun deleteToken(token: Token, onSuccess: () -> Unit) = viewModelScope.launch {
        TokenRepository.tokenDao.deleteById(token.id)
        onSuccess.invoke()
    }
}