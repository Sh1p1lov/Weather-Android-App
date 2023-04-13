package com.sh1p1lov.weatherandroidapp.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.fragment.app.Fragment
import com.sh1p1lov.weatherandroidapp.R
import com.sh1p1lov.weatherandroidapp.databinding.FragmentWeatherBinding
import com.sh1p1lov.weatherandroidapp.sharedprefs.AppPrefs
import com.sh1p1lov.weatherandroidapp.viewmodel.WeatherFragmentViewModel
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

typealias OnCurrentLocationListener = (location: Location) -> Unit

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private var _binding: FragmentWeatherBinding? = null
    private val binding: FragmentWeatherBinding get() = _binding!!
    private val vm by viewModel<WeatherFragmentViewModel>()
    private val appPref: AppPrefs = get()

    companion object {
        private const val PROGRESS_VIEW_OFFSET_DP = 120f
    }

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            getCurrentLocation(l = onCurrentLocationListener)
        } else {
            appPref.getLocationLatitude()?.let { lat ->
                vm.outdatedLocationError()
                val lon = appPref.getLocationLongitude()!!
                vm.updateData(latitude = lat, longitude = lon)

            } ?: vm.locationError()
        }
    }

    private val onCurrentLocationListener: OnCurrentLocationListener = { location ->
        Log.d("WeatherFragmentTag", location.toString())

        if (vm.error.value == WeatherFragmentViewModel.ERROR_CODE_LOCATION
            || vm.error.value == WeatherFragmentViewModel.ERROR_CODE_OUTDATED_LOCATION
        ) {
            vm.noError()
        }

        appPref.saveLocationLatitudeAndLongitude(
            latitude = location.latitude,
            longitude = location.longitude
        )

        vm.updateData(
            latitude = location.latitude,
            longitude = location.longitude
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWeatherBinding.bind(view)
        updateLocale()

        binding.btnUpdate.setOnClickListener {
            getCurrentLocation(l = onCurrentLocationListener)
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.lang_ru -> {
                    appPref.saveCurrentLanguage(AppPrefs.LANGUAGE_RU)
                    updateLocale()
                }
                R.id.lang_en -> {
                    appPref.saveCurrentLanguage(AppPrefs.LANGUAGE_EN)
                    updateLocale()
                }
            }
            true
        }

        vm.error.observe(viewLifecycleOwner) { errorCode ->
            when(errorCode) {
                WeatherFragmentViewModel.NO_ERROR -> {
                    with(binding.tvError) {
                        text = ""
                        visibility = View.GONE
                    }
                }
                WeatherFragmentViewModel.ERROR_CODE_LOCATION -> {
                    with(binding.tvError) {
                        text = requireContext().localeConfigContext().getText(R.string.location_error)
                        visibility = View.VISIBLE
                    }
                }
                WeatherFragmentViewModel.ERROR_CODE_OUTDATED_LOCATION -> {
                    with(binding.tvError) {
                        text = requireContext().localeConfigContext().getText(R.string.outdated_location_error)
                        visibility = View.VISIBLE
                    }
                }
                WeatherFragmentViewModel.ERROR_CODE_DATA_UPDATE -> {
                    with(binding.tvError) {
                        text = requireContext().localeConfigContext().getText(R.string.data_update_error)
                        visibility = View.VISIBLE
                    }
                }
            }
        }

        vm.locationNameRu.observe(viewLifecycleOwner) { locationNameRu ->
            appPref.saveLocationName(locationNameRu, AppPrefs.LANGUAGE_RU)
            if (appPref.getCurrentLanguage() == AppPrefs.LANGUAGE_RU) {
                binding.toolbar.title = locationNameRu
            }
        }

        vm.locationNameEn.observe(viewLifecycleOwner) { locationNameEn ->
            appPref.saveLocationName(locationNameEn, AppPrefs.LANGUAGE_EN)
            if (appPref.getCurrentLanguage() == AppPrefs.LANGUAGE_EN) {
                binding.toolbar.title = locationNameEn
            }
        }

        val progressViewOffsetPx =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                PROGRESS_VIEW_OFFSET_DP,
                requireContext().resources.displayMetrics
            ).toInt()
        binding.swipeContainer.setProgressViewEndTarget(true, progressViewOffsetPx)
    }

    fun updateToolbarPaddingTop(topPadding: Int) {
        binding.toolbar.updatePadding(top = topPadding)
    }

    private fun Context.localeConfigContext(): Context {
        val language = appPref.getCurrentLanguage()
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = this.resources.configuration
        config.setLocale(locale)
        return this.createConfigurationContext(config)
    }

    private fun updateLocale() {
        val configContext = requireContext().localeConfigContext()

        with(binding) {
            tvWeatherTitle.text = when(appPref.getWeatherState()) {
                AppPrefs.WEATHER_CLEAR -> configContext.getText(R.string.weather_clear)
                AppPrefs.WEATHER_CLOUDY -> configContext.getText(R.string.weather_cloudy)
                AppPrefs.WEATHER_PARTY_CLOUDY -> configContext.getText(R.string.weather_partly_cloudy)
                AppPrefs.WEATHER_PRECIPITATION -> configContext.getText(R.string.weather_precipitation)
                else -> configContext.getText(R.string.weather_clear)
            }
            tvUpdatedDateTitle.text = configContext.getText(R.string.updated_date_title)
            btnUpdate.text = configContext.getText(R.string.button_update)
            tvDropdownMenuTitle.text = configContext.getText(R.string.data_source)
            toolbar.title = appPref.getLocationName(appPref.getCurrentLanguage())

            if (vm.error.value != WeatherFragmentViewModel.NO_ERROR) {
                tvError.text = when(vm.error.value) {
                    WeatherFragmentViewModel.ERROR_CODE_LOCATION -> configContext.getText(R.string.location_error)
                    WeatherFragmentViewModel.ERROR_CODE_OUTDATED_LOCATION -> configContext.getText(R.string.outdated_location_error)
                    WeatherFragmentViewModel.ERROR_CODE_DATA_UPDATE -> configContext.getText(R.string.data_update_error)
                    else -> ""
                }
            }
        }
    }

    private fun getCurrentLocation(l: OnCurrentLocationListener) {
        when (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {

            PackageManager.PERMISSION_GRANTED -> {

                val locManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val criteria = Criteria()
                val provider = locManager.getBestProvider(criteria, true)

                provider?.let { providerName ->

                    locManager.getLastKnownLocation(providerName)?.let { lastLocation ->
                        l.invoke(lastLocation)

                    } ?: run {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            locManager.getCurrentLocation(
                                providerName,
                                null,
                                requireContext().mainExecutor
                            ) {
                                l.invoke(it)
                            }
                        } else {

                            val locationListener = object : LocationListener {
                                override fun onLocationChanged(location: Location) {
                                    l.invoke(location)
                                }

                                override fun onProviderDisabled(provider: String) {}
                                override fun onProviderEnabled(provider: String) {}
                            }

                            @Suppress("DEPRECATION")
                            locManager.requestSingleUpdate(
                                providerName,
                                locationListener,
                                Looper.getMainLooper()
                            )
                        }
                    }
                } ?: run {
                    appPref.getLocationLatitude()?.let { lat ->
                        vm.outdatedLocationError()
                        val lon = appPref.getLocationLongitude()!!
                        vm.updateData(latitude = lat, longitude = lon)

                    } ?: vm.locationError()
                }
            }

            else -> {
                locationPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}