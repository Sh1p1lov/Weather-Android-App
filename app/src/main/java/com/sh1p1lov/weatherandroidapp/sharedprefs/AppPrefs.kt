package com.sh1p1lov.weatherandroidapp.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AppPrefs(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        const val DEFAULT_LANGUAGE = "en"
        const val LANGUAGE_EN = "en"
        const val LANGUAGE_RU = "ru"
        const val UNKNOWN_LOCATION_NAME = "???"
        const val UNKNOWN_UPDATED_DATE = "???"
        const val WEATHER_CLEAR = 0
        const val WEATHER_CLOUDY = 1
        const val WEATHER_PARTY_CLOUDY = 2
        const val WEATHER_PRECIPITATION = 3
        private const val PREFERENCES_NAME = "APP_PREFERENCES"
        private const val KEY_CURRENT_LANGUAGE = "CURRENT_LANGUAGE"
        private const val KEY_LOCATION_NAME_EN = "LOCATION_NAME_EN"
        private const val KEY_LOCATION_NAME_RU = "LOCATION_NAME_RU"
        private const val KEY_LOCATION_LATITUDE = "LOCATION_LATITUDE"
        private const val KEY_LOCATION_LONGITUDE = "LOCATION_LONGITUDE"
        private const val KEY_WEATHER_STATE = "WEATHER_STATE"
        private const val KEY_CURRENT_TEMPERATURE = "CURRENT_TEMPERATURE"
        private const val KEY_UPDATED_DATE = "UPDATED_DATE"
    }

    fun saveLocationLatitudeAndLongitude(latitude: Double, longitude: Double) {
        prefs.edit(commit = true) {
            putString(KEY_LOCATION_LATITUDE, latitude.toString())
            putString(KEY_LOCATION_LONGITUDE, longitude.toString())
        }
    }

    fun getLocationLatitude(): Double? = prefs.getString(KEY_LOCATION_LATITUDE, null)?.toDouble()
    fun getLocationLongitude(): Double? = prefs.getString(KEY_LOCATION_LONGITUDE, null)?.toDouble()

    fun saveCurrentLanguage(language: String) {
        prefs.edit(commit = true) {
            putString(KEY_CURRENT_LANGUAGE, language)
        }
    }

    fun getCurrentLanguage(): String = prefs.getString(KEY_CURRENT_LANGUAGE, DEFAULT_LANGUAGE)!!

    fun saveLocationName(locationName: String, language: String) {
        when (language) {
            LANGUAGE_EN -> {
                prefs.edit(commit = true) {
                    putString(KEY_LOCATION_NAME_EN, locationName)
                }
            }
            LANGUAGE_RU -> {
                prefs.edit(commit = true) {
                    putString(KEY_LOCATION_NAME_RU, locationName)
                }
            }
        }
    }

    fun getLocationName(language: String): String {
        return when (language) {
            LANGUAGE_EN -> {
                prefs.getString(KEY_LOCATION_NAME_EN, UNKNOWN_LOCATION_NAME)!!
            }
            LANGUAGE_RU -> {
                prefs.getString(KEY_LOCATION_NAME_RU, UNKNOWN_LOCATION_NAME)!!
            }
            else -> UNKNOWN_LOCATION_NAME
        }
    }

    fun saveWeatherState(@androidx.annotation.IntRange(0, 3) weatherSate: Int) {
        prefs.edit(commit = true) {
            putInt(KEY_WEATHER_STATE, weatherSate)
        }
    }

    fun getWeatherState(): Int = prefs.getInt(KEY_WEATHER_STATE, WEATHER_CLEAR)

    fun saveCurrentTemperature(currentTemperature: Int) {
        prefs.edit(commit = true) {
            putInt(KEY_CURRENT_TEMPERATURE, currentTemperature)
        }
    }

    fun getCurrentTemperature(): Int = prefs.getInt(KEY_CURRENT_TEMPERATURE, 0)

    fun saveUpdatedDate(date: String) {
        prefs.edit(commit = true) {
            putString(KEY_UPDATED_DATE, date)
        }
    }

    fun getUpdatedDate(): String = prefs.getString(KEY_UPDATED_DATE, UNKNOWN_UPDATED_DATE)!!
}