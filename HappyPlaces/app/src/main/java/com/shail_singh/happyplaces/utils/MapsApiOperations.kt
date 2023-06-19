package com.shail_singh.happyplaces.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.util.Locale

@Suppress("DEPRECATION")
object MapsApiOperations {
    class CoordinateAddressHandler(
        context: Context, private val latitude: Double, private val longitude: Double
    ) {
        private val geocoder: Geocoder = Geocoder(context, Locale.getDefault())

        fun getAddress(): String? {
            try {
                val addressList: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0]
                    val sb: StringBuilder = StringBuilder()
                    for (i in 0..address.maxAddressLineIndex) {
                        sb.append(address.getAddressLine(i)).append(" ")
                    }
                    sb.deleteCharAt(sb.length - 1)
                    return sb.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }
    }
}