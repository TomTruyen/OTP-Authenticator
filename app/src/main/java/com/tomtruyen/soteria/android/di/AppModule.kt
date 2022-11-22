package com.tomtruyen.soteria.android.di

import com.tomtruyen.soteria.android.services.DriveService
import com.tomtruyen.soteria.android.services.FileService
import com.tomtruyen.soteria.android.ui.screens.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FileService(get())}
    single { DriveService(get(), get())}

    viewModel { SettingsViewModel(get(), get()) }
}