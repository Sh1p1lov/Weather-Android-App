package com.sh1p1lov.weatherandroidapp.di

import com.sh1p1lov.domain.usecase.GetLocationNameUseCase
import org.koin.dsl.module

val domainModule = module {

    factory { GetLocationNameUseCase(get()) }
}