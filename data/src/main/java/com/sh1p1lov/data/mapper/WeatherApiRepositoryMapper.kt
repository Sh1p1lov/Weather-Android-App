package com.sh1p1lov.data.mapper

import com.sh1p1lov.domain.model.CurrentWeather
import com.sh1p1lov.weatherapi.openmeteoapi.model.OpenMeteoApiResponse
import com.sh1p1lov.weatherapi.weatherapi.model.WeatherApiResponse

fun OpenMeteoApiResponse.mapToCurrentWeather(): CurrentWeather {
    val temp = this.current_weather.temperature
    val index = this.hourly.time.indexOf(
        this.hourly.time.first { it == this.current_weather.time }
    )
    val cloud = this.hourly.cloudcover[index]
    val precip = this.hourly.precipitation[index]

    return CurrentWeather(
        temperature = temp,
        cloud = cloud,
        precipitation = precip
    )
}

fun WeatherApiResponse.mapToCurrentWeather(): CurrentWeather {
    val temp = this.current.temp_c
    val cloud = this.current.cloud
    val precip = this.current.precip_mm

    return CurrentWeather(
        temperature = temp,
        cloud = cloud,
        precipitation = precip
    )
}