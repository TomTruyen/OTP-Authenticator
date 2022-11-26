package com.tomtruyen.soteria.wear.ui.screens.detail

import androidx.lifecycle.ViewModel
import com.tomtruyen.soteria.wear.repositories.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import kotlin.concurrent.fixedRateTimer

class TokenDetailViewModel(val id: String): ViewModel() {
    val token =TokenRepository.tokenDao.findLiveById(id)

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
}
