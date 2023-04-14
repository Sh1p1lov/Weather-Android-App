package com.sh1p1lov.geocodingapi.model

data class Address(
    val addressLine: String,
    val adminDistrict: String,
    val adminDistrict2: String,
    val countryRegion: String,
    val formattedAddress: String,
    val locality: String
)