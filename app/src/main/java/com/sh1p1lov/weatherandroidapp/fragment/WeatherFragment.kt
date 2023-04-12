package com.sh1p1lov.weatherandroidapp.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
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

typealias OnCurrentLocationListener = (location: Location) -> Unit

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private var _binding: FragmentWeatherBinding? = null
    private val binding: FragmentWeatherBinding get() = _binding!!

    companion object {
        private const val PROGRESS_VIEW_OFFSET_DP = 120f
    }

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            getCurrentLocation(l = onCurrentLocationListener)
        }
    }

    private val onCurrentLocationListener: OnCurrentLocationListener = { location ->
        Log.d("MainActivityTag", location.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWeatherBinding.bind(view)

        binding.btnUpdate.setOnClickListener {
            getCurrentLocation(l = onCurrentLocationListener)
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        locManager.getCurrentLocation(
                            providerName,
                            null,
                            requireContext().mainExecutor
                        ) {
                            l.invoke(it)
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        locManager.requestSingleUpdate(
                            providerName,
                            {
                                l.invoke(it)
                            },
                            Looper.getMainLooper()
                        )
                    }
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