package com.tomtruyen.soteria.android.repositories

import com.tomtruyen.soteria.android.App

object TokenRepository{
    val tokenDao = App.database.tokenDao()
}