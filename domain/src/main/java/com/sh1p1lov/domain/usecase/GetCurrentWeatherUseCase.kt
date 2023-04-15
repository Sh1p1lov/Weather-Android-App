package com.sh1p1lov.domain.usecase

import com.sh1p1lov.domain.model.CurrentWeather
import com.sh1p1lov.domain.repository.WeatherApiRepository
import com.sh1p1lov.domain.requestbuilder.WeatherApiRequest

class GetCurrentWeatherUseCase(private val weatherApiRepository: WeatherApiRepository) {

    suspend fun execute(request: WeatherApiRequest): CurrentWeather {
        return weatherApiRepository.getCurrentWeather(request)
    }
}