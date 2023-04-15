package com.sh1p1lov.data.repository

import com.sh1p1lov.data.mapper.mapToCurrentWeather
import com.sh1p1lov.domain.model.CurrentWeather
import com.sh1p1lov.domain.repository.WeatherApiRepository
import com.sh1p1lov.domain.requestbuilder.WeatherApiRequest
import com.sh1p1lov.weatherapi.openmeteoapi.OpenMeteoAPI
import com.sh1p1lov.weatherapi.weatherapi.WeatherAPI

class WeatherApiRepositoryImpl(
    private val openMeteoAPI: OpenMeteoAPI,
    private val weatherAPI: WeatherAPI
) : WeatherApiRepository {

    override suspend fun getCurrentWeather(request: WeatherApiRequest): CurrentWeather {
        return when(request.source) {
            WeatherApiRequest.Source.WEATHER_API -> {
                val response = weatherAPI.currentWeather(request.apiKey, request.getPoint())
                response.body()!!.mapToCurrentWeather()
            }
            else -> {
                val response = openMeteoAPI.currentWeather(request.latitude, request.longitude)
                response.body()!!.mapToCurrentWeather()
            }
        }
    }
}