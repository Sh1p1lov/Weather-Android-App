package com.sh1p1lov.domain.usecase

import com.sh1p1lov.domain.model.LocationName
import com.sh1p1lov.domain.repository.GeocodingApiRepository
import com.sh1p1lov.domain.requestbuilder.GeocodingApiRequest

class GetLocationNameUseCase(private val geocodingApiRepository: GeocodingApiRepository) {

    suspend fun execute(request: GeocodingApiRequest): LocationName {
        return geocodingApiRepository.reverseGeocode(request)
    }
}