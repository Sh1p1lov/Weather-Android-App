package com.sh1p1lov.weatherandroidapp.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.view.*
import androidx.fragment.app.Fragment
import com.sh1p1lov.weatherandroidapp.R
import com.sh1p1lov.weatherandroidapp.databinding.FragmentWeatherBinding

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private var _binding: FragmentWeatherBinding? = null
    private val binding: FragmentWeatherBinding get() = _binding!!

    companion object {
        private const val PROGRESS_VIEW_OFFSET_DP = 120f
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWeatherBinding.bind(view)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}