package com.tomtruyen.soteria.android.models

import android.app.Application

class Globals : Application() {
    companion object {
        var isLoggedIn = false
    }
}