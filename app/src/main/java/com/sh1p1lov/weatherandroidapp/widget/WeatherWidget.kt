package com.sh1p1lov.weatherandroidapp.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.sh1p1lov.domain.model.CurrentWeather
import com.sh1p1lov.domain.requestbuilder.WeatherApiRequest
import com.sh1p1lov.domain.usecase.GetCurrentWeatherUseCase
import com.sh1p1lov.weatherandroidapp.BuildConfig
import com.sh1p1lov.weatherandroidapp.R
import com.sh1p1lov.weatherandroidapp.sharedprefs.AppPrefs
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.get
import java.time.Instant
import java.util.*

class WeatherWidget : AppWidgetProvider() {

    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase = get(GetCurrentWeatherUseCase::class.java)
    private val appPref: AppPrefs = get(AppPrefs::class.java)
    private var isMyUpdate = false

    companion object {
        const val EXTRA_MY_UPDATE = "com.sh1p1lov.weatherandroidapp.EXTRA_MY_UPDATE"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val localeContext = context.localeConfigContext()

        for (appWidgetId in appWidgetIds) {
            if (isMyUpdate) {
                updateWidgetUI(localeContext, appWidgetManager, appWidgetId)
            } else {
                updateWidgetUI(localeContext, appWidgetManager, appWidgetId)
                appPref.getLocationLatitude()?.let { latitude ->
                    val longitude = appPref.getLocationLongitude()!!
                    val source = appPref.getCurrentSource()

                    val exceptionHandler = CoroutineExceptionHandler { _, _ ->
                    }

                    CoroutineScope(Dispatchers.Main).launch(exceptionHandler) {
                        val currentWeather = getCurrentWeather(latitude, longitude, source)

                        val currentTemperature = currentWeather.temperature
                        val weatherState = if (currentWeather.cloud <= 10) {
                            AppPrefs.WEATHER_CLEAR
                        } else if (currentWeather.precipitation >= 0.2) {
                            AppPrefs.WEATHER_PRECIPITATION
                        } else if (currentWeather.cloud in 11..89) {
                            AppPrefs.WEATHER_PARTY_CLOUDY
                        } else {
                            AppPrefs.WEATHER_CLOUDY
                        }
                        val updatedDate = Instant.now().toString()

                        appPref.saveCurrentTemperature(currentTemperature.toString())
                        appPref.saveWeatherState(weatherState)
                        appPref.saveUpdatedDate(updatedDate)

                        updateWidgetUI(localeContext, appWidgetManager, appWidgetId)
                    }
                }
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent) {
        isMyUpdate = intent.getBooleanExtra(EXTRA_MY_UPDATE, false)
        super.onReceive(context, intent)
    }

    private fun updateWidgetUI(
        localeContext: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val remoteViews = RemoteViews(localeContext.packageName, R.layout.widget_weather)

        val currentLanguage = appPref.getCurrentLanguage()
        val locationName = appPref.getLocationName(currentLanguage)
        val currentTemperature = appPref.getCurrentTemperature()
        val currentWeatherState = appPref.getWeatherState()
        val updatedDate = appPref.getUpdatedDate()

        remoteViews.apply {
            setTextViewText(R.id.tv_location_title, locationName)
            setTextViewText(R.id.tv_current_temperature, "$currentTemperature℃")
            setTextViewText(R.id.tv_updated_date, updatedDate)
        }


        when(currentWeatherState) {
            AppPrefs.WEATHER_CLEAR -> {
                val weatherTitle = localeContext.getText(R.string.weather_clear)
                remoteViews.setTextViewText(R.id.tv_weather_title, weatherTitle)
                remoteViews.setImageViewResource(R.id.ic_weather, R.drawable.icon_clear)
            }
            AppPrefs.WEATHER_CLOUDY -> {
                val weatherTitle = localeContext.getText(R.string.weather_cloudy)
                remoteViews.setTextViewText(R.id.tv_weather_title, weatherTitle)
                remoteViews.setImageViewResource(R.id.ic_weather, R.drawable.icon_cloudy)
            }
            AppPrefs.WEATHER_PARTY_CLOUDY -> {
                val weatherTitle = localeContext.getText(R.string.weather_partly_cloudy)
                remoteViews.setTextViewText(R.id.tv_weather_title, weatherTitle)
                remoteViews.setImageViewResource(R.id.ic_weather, R.drawable.icon_partly_cloudy)
            }
            AppPrefs.WEATHER_PRECIPITATION -> {
                val weatherTitle = localeContext.getText(R.string.weather_precipitation)
                remoteViews.setTextViewText(R.id.tv_weather_title, weatherTitle)
                remoteViews.setImageViewResource(R.id.ic_weather, R.drawable.icon_precipitation)
            }
        }

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
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

    private fun Context.localeConfigContext(): Context {
        val language = appPref.getCurrentLanguage()
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = this.resources.configuration
        config.setLocale(locale)
        return this.createConfigurationContext(config)
    }
}