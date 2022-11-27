package com.tomtruyen.soteria.android.ui.screens

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.appwise.core.ui.base.BaseViewModel
import com.tomtruyen.soteria.android.repositories.TokenRepository
import com.tomtruyen.soteria.android.services.WearSyncService
import com.tomtruyen.soteria.common.data.entities.Token
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.timerTask

class TokenViewModel : ViewModel() {
    val tokens = TokenRepository.tokenDao.findAllLive()
    var selectedToken: Token? = null

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
    fun saveToken(context: Context, token: Token, onSuccess: () -> Unit) = viewModelScope.launch {
        TokenRepository.tokenDao.insert(token)
        val tokens = TokenRepository.tokenDao.findAllEntities() ?: emptyList()
        WearSyncService.syncTokens(context, tokens)
        onSuccess.invoke()
    }

    fun deleteToken(context: Context, token: Token, onSuccess: () -> Unit) = viewModelScope.launch {
        TokenRepository.tokenDao.deleteById(token.id)
        onSuccess.invoke()
        WearSyncService.syncTokens(context, tokens.last())
    }
}