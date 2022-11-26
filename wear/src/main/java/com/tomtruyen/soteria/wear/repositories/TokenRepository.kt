package com.tomtruyen.soteria.wear.repositories

import com.tomtruyen.soteria.wear.App

object TokenRepository {
    val tokenDao = App.database.tokenDao()
}