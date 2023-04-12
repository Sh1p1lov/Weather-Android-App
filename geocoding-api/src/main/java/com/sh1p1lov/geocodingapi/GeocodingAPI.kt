package com.sh1p1lov.geocodingapi

import com.sh1p1lov.geocodingapi.model.GeocodingApiResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GeocodingAPI {

    @GET("Locations/{point}?")
    suspend fun reverseGeocode(
        @Query("key") apiKey: String,
        @Path("point") point: String,      // example: 47.64054,-122.12934 (lat,long)
        @Query("c") culture: String        // ru, en-US
    ): Response<GeocodingApiResponse>

    companion object {
        private const val BASE_URL = "http://dev.virtualearth.net/REST/v1/"
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}