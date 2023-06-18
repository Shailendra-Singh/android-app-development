package com.shail_singh.happyplaces.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.shail_singh.happyplaces.AppConstants
import com.shail_singh.happyplaces.R
import com.shail_singh.happyplaces.databinding.ActivityMapBinding
import com.shail_singh.happyplaces.models.HappyPlaceModel

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    private lateinit var binding: ActivityMapBinding
    private var happyPlaceModel: HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbMap)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbMap.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        var currentCardModel: HappyPlaceModel? = null
        if (intent.hasExtra(AppConstants.CURRENT_CARD_ITEM)) {
            currentCardModel = intent.parcelable(AppConstants.CURRENT_CARD_ITEM)
        }

        if (currentCardModel != null) {
            supportActionBar?.title = currentCardModel.location
            this.happyPlaceModel = currentCardModel

            val supportMapFragment: SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment

            supportMapFragment.getMapAsync(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val lat = this.happyPlaceModel?.latitude!!
        val lon = this.happyPlaceModel?.longitude!!
        val position: LatLng = LatLng(lat, lon)
        googleMap.addMarker(
            MarkerOptions().position(position).title(this.happyPlaceModel?.location)
        )

        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 12f)
        googleMap.animateCamera(newLatLngZoom)
    }
}