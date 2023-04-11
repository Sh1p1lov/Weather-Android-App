package com.sh1p1lov.weatherandroidapp.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sh1p1lov.weatherandroidapp.databinding.ActivityMainBinding
import com.sh1p1lov.weatherandroidapp.fragment.WeatherFragment

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView).apply {
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, windowInsets ->

            if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())
            ) {
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            }

            updateWeatherFragmentToolbarPaddingTop(windowInsets)

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun updateWeatherFragmentToolbarPaddingTop(insets: WindowInsetsCompat) {
        val childFragment = supportFragmentManager.findFragmentById(binding.fragmentContainer.id)

        if (childFragment is WeatherFragment) {
            val topPadding = insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars()).top
            childFragment.updateToolbarPaddingTop(topPadding)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}