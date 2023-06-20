package com.shail_singh.weatherapp

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppConstants {
    const val BASE_URL: String = "https://api.openweathermap.org/data/"
    const val UNIT: String = "metric"
    const val LOG_API_TAG: String = "API"
    const val PREFERENCE_NAME: String = "WeatherAppPreference"
    const val WEATHER_RESPONSE_DATA: String = "WeatherApiResponseData"

    fun getDateString(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(date).toString()
    }

    fun getUnit(unit: String): String {
        var value = "°C"
        if (unit in setOf("US", "MM", "LR")) {
            value = "°F"
        }
        return value
    }

    @Suppress("UNREACHABLE_CODE")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            else -> return false
        }
    }

    fun isLocationAvailable(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}