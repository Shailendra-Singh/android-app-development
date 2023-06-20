package com.shail_singh.weatherapp.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.shail_singh.weatherapp.AppConstants
import com.shail_singh.weatherapp.models.WeatherApiModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiHandler(
    context: Context, latitude: Double, longitude: Double
) {

    private lateinit var onApiResponseCallback: IOnApiResponseCallback

    interface IOnApiResponseCallback {
        fun onApiResponse(response: WeatherApiModel.WeatherResponse)
    }

    fun setRequestApiResponseListener(responseListenerCallback: IOnApiResponseCallback) {
        this.onApiResponseCallback = responseListenerCallback
    }

    init {
        if (AppConstants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder().baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()

            val service: WeatherService = retrofit.create(WeatherService::class.java)

            val apiKey =
                context.applicationContext.applicationInfo.metaData.getString("org.openweathermap.currentweather.API_KEY")

            val listCall: Call<WeatherApiModel.WeatherResponse> = service.getWeather(
                latitude, longitude, AppConstants.UNIT, apiKey
            )

            listCall.enqueue(object : Callback<WeatherApiModel.WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherApiModel.WeatherResponse>,
                    response: Response<WeatherApiModel.WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherList: WeatherApiModel.WeatherResponse = response.body()!!
                        this@WeatherApiHandler.onApiResponseCallback.onApiResponse(weatherList)
                    } else {
                        Toast.makeText(context, "Content Unavailable!", Toast.LENGTH_LONG).show()
                        when (response.code()) {
                            400 -> Log.e(AppConstants.LOG_API_TAG, "Bad Connection")
                            404 -> Log.e(AppConstants.LOG_API_TAG, "Error 400: Not Found")
                            else -> Log.e(AppConstants.LOG_API_TAG, "API Error")
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherApiModel.WeatherResponse>, t: Throwable) {
                    Log.e(AppConstants.LOG_API_TAG, "API ERROR")
                }
            })

        } else {
            Toast.makeText(context, "Network not available", Toast.LENGTH_LONG).show()
        }
    }
}