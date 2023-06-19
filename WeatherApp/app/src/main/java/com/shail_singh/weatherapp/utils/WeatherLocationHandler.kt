package com.shail_singh.weatherapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class WeatherLocationHandler(private val activity: Activity) {

    interface IOnLocationResultListener {
        fun onLocationResult(locationResult: LocationResult)
    }

    @SuppressLint("MissingPermission", "InlinedApi")
    fun setLocationResultListener(onLocationResultListener: IOnLocationResultListener) {

        getLocationPermissionManager()?.requestPermissions(object :
            PermissionsManager.IPermissionsCheckedListener {
            override fun onPermissionsChecked() {
                val fusedLocationProviderClient: FusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(activity)

                val locationRequest =
                    com.google.android.gms.location.LocationRequest.Builder(1000).apply {
                        setMaxUpdates(1)
                        setPriority(LocationRequest.QUALITY_HIGH_ACCURACY)
                    }.build()

                val locationCallback: LocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        onLocationResultListener.onLocationResult(locationResult)
                    }
                }

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.getMainLooper()
                )
            }
        })
    }

    // region Initialize Location
    @RequiresApi(Build.VERSION_CODES.S)
    private fun getLocationPermissionManager(): PermissionsManager? {
        if (!isLocationAvailable()) {
            Toast.makeText(
                activity,
                "Your Location provider is turned off." + " Please turn it on in settings",
                Toast.LENGTH_LONG
            ).show()
            activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            return PermissionsManager(
                activity, "LOCATION", mutableListOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
        return null
    }

    private fun isLocationAvailable(): Boolean {
        val locationManager: LocationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    // endregion
}