package com.shail_singh.happyplaces.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shail_singh.happyplaces.AppConstants
import com.shail_singh.happyplaces.databinding.ActivityHappyPlaceDetailsBinding
import com.shail_singh.happyplaces.models.HappyPlaceModel
import java.io.Serializable

class HappyPlaceDetailsActivity : AppCompatActivity() {

    private var binding: ActivityHappyPlaceDetailsBinding? = null

    /*https://stackoverflow.com/questions/72571804/getserializableextra-and-getparcelableextra-deprecated-what-is-the-alternative*/
    private inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
            key, T::class.java
        )

        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
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
            currentCardModel = intent.serializable(AppConstants.CURRENT_CARD_ITEM)
        }
        if (currentCardModel != null) {
            populateCardDetails(currentCardModel)
        }
    }

    private fun populateCardDetails(item: HappyPlaceModel) {
        binding?.ivPlaceImage?.setImageURI(Uri.parse(item.imagePath))
        binding?.tvDate?.text = item.date
        binding?.tvTitle?.text = item.title
        binding?.tvDescription?.text = item.description
    }
}