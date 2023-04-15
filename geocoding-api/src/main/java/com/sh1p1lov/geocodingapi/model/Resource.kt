package com.sh1p1lov.geocodingapi.model

data class Resource(
    val __type: String,
    val address: Address,
    val bbox: List<Double>,
    val confidence: String,
    val entityType: String,
    val geocodePoints: List<GeocodePoint>,
    val matchCodes: List<String>,
    val name: String,
    val point: Point
)