package com.sh1p1lov.weatherapi.openmeteoapi.model

data class Hourly(
    val cloudcover: List<Int>,
    val precipitation: List<Double>,
    val time: List<String>
)