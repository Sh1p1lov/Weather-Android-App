package com.sh1p1lov.weatherandroidapp.app

import android.app.Application
import com.sh1p1lov.weatherandroidapp.di.appModule
import com.sh1p1lov.weatherandroidapp.di.dataModule
import com.sh1p1lov.weatherandroidapp.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule, dataModule, domainModule)
        }
    }
}