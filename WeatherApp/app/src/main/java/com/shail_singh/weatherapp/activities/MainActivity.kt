package com.shail_singh.weatherapp.activities

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson
import com.shail_singh.weatherapp.AppConstants
import com.shail_singh.weatherapp.R
import com.shail_singh.weatherapp.api.WeatherApiHandler
import com.shail_singh.weatherapp.databinding.ActivityMainBinding
import com.shail_singh.weatherapp.models.WeatherApiModel
import com.shail_singh.weatherapp.utils.WeatherLocationHandler

class MainActivity : AppCompatActivity(), WeatherApiHandler.IOnApiResponseCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressDialog: Dialog
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)

        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.dialog_custom_progress)

        refreshUI()
    }

    private fun refreshUI() {
        val dataFromSharedPreferences = getFromSharedPreferences()
        if (dataFromSharedPreferences != null) {
            initializeUI(dataFromSharedPreferences)
        } else {
            requestWeatherLocationData()
        }
    }

    private fun requestWeatherLocationData() {
        progressDialog.show()
        val weatherLocationHandler = WeatherLocationHandler(this)
        weatherLocationHandler.setLocationResultListener(object :
            WeatherLocationHandler.IOnLocationResultListener {
            override fun onLocationResult(locationResult: LocationResult) {
                val lat = locationResult.lastLocation!!.latitude
                val lon = locationResult.lastLocation!!.longitude
                WeatherApiHandler(
                    this@MainActivity, lat, lon
                ).setRequestApiResponseListener(this@MainActivity)
            }
        })
    }

    override fun onApiResponse(response: WeatherApiModel.WeatherResponse) {
        saveToSharedPreferences(response)
        initializeUI(response)
    }

    private fun saveToSharedPreferences(any: Any) {
        val weatherResponseJsonString = Gson().toJson(any)
        val editor = sharedPreferences.edit()
        editor.putString(AppConstants.WEATHER_RESPONSE_DATA, weatherResponseJsonString)
        editor.apply()
    }

    private fun getFromSharedPreferences(): WeatherApiModel.WeatherResponse? {
        progressDialog.show()
        val weatherResponseJsonString = sharedPreferences.getString(
            AppConstants.WEATHER_RESPONSE_DATA, null
        )

        if (weatherResponseJsonString != null) {
            return Gson().fromJson(
                weatherResponseJsonString, WeatherApiModel.WeatherResponse::class.java
            )
        }

        return null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @Suppress("UNREACHABLE_CODE")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                requestWeatherLocationData()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun initializeUI(weatherResponse: WeatherApiModel.WeatherResponse) {
        Log.i(AppConstants.LOG_API_TAG, weatherResponse.toString())
        for (i in weatherResponse.weather.indices) {
            val item = weatherResponse.weather[i]

            binding.tvWeatherLabel.text = item.main
            binding.tvWeatherValue.text = item.description

            when (item.icon) {
                in setOf(
                    "02d", "03d", "04d"
                ) -> binding.ivWeatherIcon.setImageResource(R.drawable.cloud)

                in setOf(
                    "02n", "03n", "04n"
                ) -> binding.ivWeatherIcon.setImageResource(R.drawable.cloudy_night)


                "01d" -> binding.ivWeatherIcon.setImageResource(R.drawable.sunny)
                "01n" -> binding.ivWeatherIcon.setImageResource(R.drawable.clear_night)
                in setOf(
                    "9d", "9n", "10d", "10n"
                ) -> binding.ivWeatherIcon.setImageResource(R.drawable.rain)

                in setOf("11d", "11n") -> binding.ivWeatherIcon.setImageResource(R.drawable.storm)
                in setOf("50d", "50n") -> binding.ivWeatherIcon.setImageResource(R.drawable.mist)
                in setOf(
                    "13d", "13n"
                ) -> binding.ivWeatherIcon.setImageResource(R.drawable.snowflake)
            }
        }

        // Temperature
        "${weatherResponse.main.temp_max} ${AppConstants.getUnit(application.resources.configuration.locales.toString())}".also {
            binding.tvTemperatureLabel.text = it
        }

        "${weatherResponse.main.temp_min} ${AppConstants.getUnit(application.resources.configuration.locales.toString())}".also {
            binding.tvTemperatureValue.text = it
        }

        // Humidity
        "${weatherResponse.main.humidity}".also {
            binding.tvHumidityLabel.text = it
        }

        "Feels like ${weatherResponse.main.temp}".also {
            binding.tvHumidityValue.text = it
        }

        // Wind
        "${weatherResponse.wind.speed}".also {
            binding.tvWindLabel.text = it
        }

        "MPH".also {
            binding.tvWindValue.text = it
        }

        // Location
        "${weatherResponse.name}, ${weatherResponse.sys.country}".also {
            binding.tvLocationLabel.text = it
        }

        // Sunrise
        AppConstants.getDateString(weatherResponse.sys.sunrise).also {
            binding.tvSunriseLabel.text = it
        }

        // Sunset
        AppConstants.getDateString(weatherResponse.sys.sunset).also {
            binding.tvSunsetLabel.text = it
        }

        // Dismiss Dialog
        progressDialog.dismiss()
    }
}