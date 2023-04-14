package com.sh1p1lov.weatherapi.weatherapi

import com.sh1p1lov.weatherapi.weatherapi.model.WeatherApiResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    //https://api.weatherapi.com/v1/current.json?key={KEY_API}&q=44.972973,34.052473
    @GET("current.json?")
    suspend fun currentWeather(
        @Query("key") key: String,
        @Query("q") point: String
    ): Response<WeatherApiResponse>

    companion object {
        private const val BASE_URL = "https://api.weatherapi.com/v1/"
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}