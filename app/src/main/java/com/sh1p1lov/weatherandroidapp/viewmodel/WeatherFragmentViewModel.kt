package com.sh1p1lov.weatherandroidapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sh1p1lov.domain.requestbuilder.GeocodingApiRequest
import com.sh1p1lov.domain.usecase.GetLocationNameUseCase
import com.sh1p1lov.weatherandroidapp.BuildConfig
import kotlinx.coroutines.*

class WeatherFragmentViewModel(
    private val getLocationNameUseCase: GetLocationNameUseCase
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

    private val mutableError = MutableLiveData<Int>(NO_ERROR)
    val error: LiveData<Int> = mutableError

    fun noError() { mutableError.value = NO_ERROR }
    fun locationError() { mutableError.value = ERROR_CODE_LOCATION }
    fun outdatedLocationError() { mutableError.value = ERROR_CODE_OUTDATED_LOCATION }
    fun dataUpdateError() { mutableError.value = ERROR_CODE_DATA_UPDATE }

    fun updateData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            updateGeocoding(latitude, longitude)
        }
    }

    private suspend fun updateGeocoding(latitude: Double, longitude: Double) {
        val geocodingReqBuilder = GeocodingApiRequest.Builder(BuildConfig.GEOCODING_API_KEY)

        val geocodingRequestRu = geocodingReqBuilder
            .setCulture(GeocodingApiRequest.Culture.RU)
            .build(latitude, longitude)

        val geocodingRequestEn = geocodingReqBuilder
            .setCulture(GeocodingApiRequest.Culture.EN)
            .build(latitude, longitude)

        val locationNames =
        withContext(Dispatchers.IO) {
            awaitAll(
                async { getLocationNameUseCase.execute(geocodingRequestRu) },
                async { getLocationNameUseCase.execute(geocodingRequestEn) }
            )
        }
        mutableLocationNameRu.value = locationNames[0].name
        mutableLocationNameEn.value = locationNames[1].name
    }
}