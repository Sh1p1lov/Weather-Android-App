package com.sh1p1lov.geocodingapi.model

data class GeocodingApiResponse(
    val authenticationResultCode: String,
    val brandLogoUri: String,
    val copyright: String,
    val resourceSets: List<ResourceSet>,
    val statusCode: Int,
    val statusDescription: String,
    val traceId: String
)