package com.tomtruyen.soteria.wear

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.tomtruyen.soteria.common.data.AppDatabase

class App: Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var mContext: Context

        val database by lazy {
            AppDatabase.getDatabase(mContext)
        }
    }

    override fun onCreate() {
        super.onCreate()

        mContext = this
    }
}