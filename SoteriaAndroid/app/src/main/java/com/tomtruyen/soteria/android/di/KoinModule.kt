package com.tomtruyen.soteria.android.di

import com.tomtruyen.soteria.android.ui.screens.TokenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinModule = module {
    viewModel { TokenViewModel() }
}