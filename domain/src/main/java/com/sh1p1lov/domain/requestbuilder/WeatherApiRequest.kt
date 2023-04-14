package com.sh1p1lov.domain.requestbuilder

class WeatherApiRequest private constructor(
    val latitude: Double,
    val longitude: Double,
    val apiKey: String,
    val source: Source
){

    enum class Source {
        OPEN_METEO_API,
        WEATHER_API
    }

    class Builder {
        private var defSource = Source.OPEN_METEO_API
        private var defApiKey = ""

        fun useWeatherApi(apiKey: String) = apply {
            defSource = Source.WEATHER_API
            defApiKey = apiKey
        }

        fun build(latitude: Double, longitude: Double): WeatherApiRequest {
            return WeatherApiRequest(latitude, longitude, defApiKey, defSource)
        }
    }
}