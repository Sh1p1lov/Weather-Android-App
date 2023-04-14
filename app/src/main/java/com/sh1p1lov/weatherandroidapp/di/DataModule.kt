package com.sh1p1lov.weatherandroidapp.di

import com.sh1p1lov.data.repository.GeocodingApiRepositoryImpl
import com.sh1p1lov.data.repository.WeatherApiRepositoryImpl
import com.sh1p1lov.domain.repository.GeocodingApiRepository
import com.sh1p1lov.domain.repository.WeatherApiRepository
import com.sh1p1lov.geocodingapi.GeocodingAPI
import com.sh1p1lov.weatherapi.openmeteoapi.OpenMeteoAPI
import com.sh1p1lov.weatherapi.weatherapi.WeatherAPI
import org.koin.dsl.module

val dataModule = module {

    single<GeocodingAPI> { GeocodingAPI.retrofit.create(GeocodingAPI::class.java) }

    single<GeocodingApiRepository> { GeocodingApiRepositoryImpl(get()) }

    single<OpenMeteoAPI> { OpenMeteoAPI.retrofit.create(OpenMeteoAPI::class.java) }

    single<WeatherAPI> { WeatherAPI.retrofit.create(WeatherAPI::class.java) }

    single<WeatherApiRepository> { WeatherApiRepositoryImpl(get(), get()) }
}