package com.sh1p1lov.weatherapi.openmeteoapi

import com.sh1p1lov.weatherapi.openmeteoapi.model.OpenMeteoApiResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoAPI {

    //https://api.open-meteo.com/v1/forecast?latitude=44.972973&longitude=34.052473&current_weather=true&&hourly=cloudcover,precipitation
    @GET("forecast?current_weather=true&hourly=cloudcover,precipitation")
    suspend fun currentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<OpenMeteoApiResponse>

    companion object {
        private const val BASE_URL = "https://api.open-meteo.com/v1/"
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}