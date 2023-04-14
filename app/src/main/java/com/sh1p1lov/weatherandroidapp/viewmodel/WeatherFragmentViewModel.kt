package com.sh1p1lov.weatherandroidapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sh1p1lov.domain.model.CurrentWeather
import com.sh1p1lov.domain.model.LocationName
import com.sh1p1lov.domain.requestbuilder.GeocodingApiRequest
import com.sh1p1lov.domain.requestbuilder.WeatherApiRequest
import com.sh1p1lov.domain.usecase.GetCurrentWeatherUseCase
import com.sh1p1lov.domain.usecase.GetLocationNameUseCase
import com.sh1p1lov.weatherandroidapp.BuildConfig
import com.sh1p1lov.weatherandroidapp.sharedprefs.AppPrefs
import kotlinx.coroutines.*
import java.time.Instant

class WeatherFragmentViewModel(
    private val getLocationNameUseCase: GetLocationNameUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
) : ViewModel() {

    companion object {
        const val NO_ERROR = 0
        const val ERROR_CODE_LOCATION = 1
        const val ERROR_CODE_OUTDATED_LOCATION = 2
        const val ERROR_CODE_DATA_UPDATE = 3
    }

    private val mutableLocationNameRu = MutableLiveData<String>()
    val locationNameRu: LiveData<String> = mutableLocationNameRu

    private val mutableLocationNameEn = MutableLiveData<String>()
    val locationNameEn: LiveData<String> = mutableLocationNameEn

    private val mutableTemperature = MutableLiveData<Double>()
    val temperature: LiveData<Double> = mutableTemperature

    private val mutableWeatherState = MutableLiveData<Int>()
    val weatherState: LiveData<Int> = mutableWeatherState

    private val mutableUpdatedDate = MutableLiveData<String>()
    val updatedDate: LiveData<String> = mutableUpdatedDate

    private val mutableError = MutableLiveData<Int>(NO_ERROR)
    val error: LiveData<Int> = mutableError

    private val mutablePageLoaded = MutableLiveData<Boolean>()
    val pageLoaded: LiveData<Boolean> = mutablePageLoaded

    fun noError() { mutableError.value = NO_ERROR }
    fun locationError() { mutableError.value = ERROR_CODE_LOCATION }
    fun outdatedLocationError() { mutableError.value = ERROR_CODE_OUTDATED_LOCATION }
    fun dataUpdateError() { mutableError.value = ERROR_CODE_DATA_UPDATE }

    fun setCurrentTemperature(temp: Double) {
        mutableTemperature.value = temp
    }

    fun setUpdatedDate(date: String) {
        mutableUpdatedDate.value = date
    }

    fun updateData(latitude: Double, longitude: Double, source: Int) {

        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            dataUpdateError()
        }

        viewModelScope.launch(exceptionHandler) {
            val geocoding = async { updateGeocoding(latitude, longitude) }
            val currentWeatherReq = async { getCurrentWeather(latitude, longitude, source) }

            val locationNames = geocoding.await()
            val currentWeather = currentWeatherReq.await()

            mutableLocationNameRu.value = locationNames[0].name
            mutableLocationNameEn.value = locationNames[1].name
            mutableTemperature.value = currentWeather.temperature
            mutableWeatherState.value =
                if (currentWeather.cloud <= 10) {
                    AppPrefs.WEATHER_CLEAR
                } else if (currentWeather.precipitation >= 0.2) {
                    AppPrefs.WEATHER_PRECIPITATION
                } else if (currentWeather.cloud in 11..89) {
                    AppPrefs.WEATHER_PARTY_CLOUDY
                } else {
                    AppPrefs.WEATHER_CLOUDY
                }

            mutableUpdatedDate.value = Instant.now().toString()
            mutablePageLoaded.value = true
        }
    }

    private suspend fun getCurrentWeather(latitude: Double, longitude: Double, source: Int): CurrentWeather {
        val weatherReqBuilder = WeatherApiRequest.Builder()

        val request = when(source) {
            AppPrefs.SOURCE_WEATHER_API -> {
                weatherReqBuilder
                    .useWeatherApi(BuildConfig.WEATHER_API_KEY)
                    .build(latitude, longitude)
            }
            else -> {
                weatherReqBuilder.build(latitude, longitude)
            }
        }

        return withContext(Dispatchers.IO) { getCurrentWeatherUseCase.execute(request) }
    }

    private suspend fun updateGeocoding(latitude: Double, longitude: Double): List<LocationName> {
        val geocodingReqBuilder = GeocodingApiRequest.Builder(BuildConfig.GEOCODING_API_KEY)

        val geocodingRequestRu = geocodingReqBuilder
            .setCulture(GeocodingApiRequest.Culture.RU)
            .build(latitude, longitude)

        val geocodingRequestEn = geocodingReqBuilder
            .setCulture(GeocodingApiRequest.Culture.EN)
            .build(latitude, longitude)

        return withContext(Dispatchers.IO) {
            awaitAll(
                async { getLocationNameUseCase.execute(geocodingRequestRu) },
                async { getLocationNameUseCase.execute(geocodingRequestEn) }
            )
        }
    }
}