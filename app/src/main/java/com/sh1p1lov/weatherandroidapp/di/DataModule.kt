package com.sh1p1lov.weatherandroidapp.di

import com.sh1p1lov.data.repository.GeocodingApiRepositoryImpl
import com.sh1p1lov.domain.repository.GeocodingApiRepository
import com.sh1p1lov.geocodingapi.GeocodingAPI
import org.koin.dsl.module

val dataModule = module {

    single<GeocodingAPI> { GeocodingAPI.retrofit.create(GeocodingAPI::class.java) }

    single<GeocodingApiRepository> { GeocodingApiRepositoryImpl(get()) }
}