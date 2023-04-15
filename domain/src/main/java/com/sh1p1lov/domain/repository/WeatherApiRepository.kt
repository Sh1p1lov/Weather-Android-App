package com.sh1p1lov.domain.repository

import com.sh1p1lov.domain.model.CurrentWeather
import com.sh1p1lov.domain.requestbuilder.WeatherApiRequest

interface WeatherApiRepository {
    suspend fun getCurrentWeather(request: WeatherApiRequest): CurrentWeather
}