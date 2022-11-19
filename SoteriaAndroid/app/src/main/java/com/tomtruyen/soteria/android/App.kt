package com.tomtruyen.soteria.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.tomtruyen.soteria.android.di.koinModule
import com.tomtruyen.soteria.common.data.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

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

        startKoin {
            androidContext(this@App)
            modules(koinModule)
        }
    }
}