package com.sh1p1lov.domain.requestbuilder

class GeocodingApiRequest private constructor(
    val apiKey: String,
    val point: String,
    val culture: String
) {

    enum class Culture(val culture: String) {
        EN("en-US"),
        RU("ru")
    }

    class Builder(private val keyApi: String) {
        private var defCulture = Culture.EN

        fun setCulture(culture: Culture) = apply { defCulture = culture }

        fun build(latitude: Double, longitude: Double): GeocodingApiRequest {
            val point = "$latitude,$longitude"
            return GeocodingApiRequest(keyApi, point, defCulture.culture)
        }
    }
}