package com.sh1p1lov.geocodingapi.model

data class GeocodePoint(
    val calculationMethod: String,
    val coordinates: List<Double>,
    val type: String,
    val usageTypes: List<String>
)