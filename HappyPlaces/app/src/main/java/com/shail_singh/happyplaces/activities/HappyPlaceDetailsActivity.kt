package com.shail_singh.happyplaces.activities

import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.shail_singh.happyplaces.AppConstants
import com.shail_singh.happyplaces.databinding.ActivityHappyPlaceDetailsBinding
import com.shail_singh.happyplaces.models.HappyPlaceModel

class HappyPlaceDetailsActivity : AppCompatActivity() {

    private var binding: ActivityHappyPlaceDetailsBinding? = null

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.tbShowPlaceDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.tbShowPlaceDetails?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        var currentCardModel: HappyPlaceModel? = null
        if (intent.hasExtra(AppConstants.CURRENT_CARD_ITEM)) {
            currentCardModel = intent.parcelable(AppConstants.CURRENT_CARD_ITEM)
        }
        if (currentCardModel != null) {
            populateCardDetails(currentCardModel)
        }

        binding?.btnViewOnMap?.setOnClickListener {
            val intent = Intent(this@HappyPlaceDetailsActivity, MapActivity::class.java)
            intent.putExtra(AppConstants.CURRENT_CARD_ITEM, currentCardModel)
            startActivity(intent)
        }
    }

    private fun populateCardDetails(item: HappyPlaceModel) {
        binding?.ivPlaceImage?.setImageURI(Uri.parse(item.imagePath))
        binding?.tvDate?.text = item.date
        binding?.tvTitle?.text = item.title
        binding?.tvDescription?.text = item.description
        binding?.tvLocation?.text = item.location
    }
}