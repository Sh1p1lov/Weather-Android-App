package com.sh1p1lov.data.mapper

import com.sh1p1lov.domain.model.LocationName
import com.sh1p1lov.geocodingapi.model.GeocodingApiResponse

fun GeocodingApiResponse.mapToLocationName(): LocationName {

    val resourceSet = this.resourceSets
    val locName =
        if (resourceSet.size > 0
            && resourceSet[0].resources.size > 0
        ) resourceSet[0].resources[0].address.addressLine
        else ""

    return LocationName(
        name = locName
    )
}