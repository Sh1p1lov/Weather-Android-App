package com.sh1p1lov.data.repository

import com.sh1p1lov.data.mapper.mapToLocationName
import com.sh1p1lov.domain.model.LocationName
import com.sh1p1lov.domain.repository.GeocodingApiRepository
import com.sh1p1lov.domain.requestbuilder.GeocodingApiRequest
import com.sh1p1lov.geocodingapi.GeocodingAPI

class GeocodingApiRepositoryImpl(private val geocodingApi: GeocodingAPI) : GeocodingApiRepository {
    override suspend fun reverseGeocode(request: GeocodingApiRequest): LocationName {
        val response = geocodingApi.reverseGeocode(
            apiKey = request.apiKey,
            point = request.point,
            culture = request.culture
        )

        return response.body()!!.mapToLocationName()
    }
}