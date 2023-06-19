package com.shail_singh.weatherapp.activities

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationResult
import com.shail_singh.weatherapp.databinding.ActivityMainBinding
import com.shail_singh.weatherapp.utils.WeatherLocationHandler

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val weatherLocationHandler = WeatherLocationHandler(this)
        weatherLocationHandler.setLocationResultListener(object :
            WeatherLocationHandler.IOnLocationResultListener {
            override fun onLocationResult(locationResult: LocationResult) {

            }
        })
    }
}