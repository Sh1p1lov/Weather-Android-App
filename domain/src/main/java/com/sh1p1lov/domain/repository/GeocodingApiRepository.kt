package com.sh1p1lov.domain.repository

import com.sh1p1lov.domain.model.LocationName
import com.sh1p1lov.domain.requestbuilder.GeocodingApiRequest

interface GeocodingApiRepository {
    suspend fun reverseGeocode(request: GeocodingApiRequest): LocationName
}