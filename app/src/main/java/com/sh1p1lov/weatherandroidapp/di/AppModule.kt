package com.sh1p1lov.weatherandroidapp.di

import com.sh1p1lov.weatherandroidapp.sharedprefs.AppPrefs
import com.sh1p1lov.weatherandroidapp.viewmodel.WeatherFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { WeatherFragmentViewModel(get(), get()) }

    single { AppPrefs(get()) }
}