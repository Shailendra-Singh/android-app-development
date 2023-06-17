package com.shail_singh.happyplaces.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.shail_singh.happyplaces.AppConstants
import com.shail_singh.happyplaces.adapters.HappyPlaceItemAdapter
import com.shail_singh.happyplaces.database.DatabaseHandler
import com.shail_singh.happyplaces.databinding.ActivityMainBinding
import com.shail_singh.happyplaces.models.HappyPlaceModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.fabAddHappyPlace?.setOnClickListener {
            startActivity(Intent(this, AddHappyPlaceActivity::class.java))
        }

        val dbHandler = DatabaseHandler(application)
        lifecycleScope.launch {
            dbHandler.fetchAllPlaces().collect {
                populateHappyPlacesView(it)
            }
        }


    }

    private fun populateHappyPlacesView(happyPlaces: List<HappyPlaceModel>) {
        if (happyPlaces.isNotEmpty()) {

            val happyPlaceItemAdapter = HappyPlaceItemAdapter(happyPlaces)
            happyPlaceItemAdapter.setOnClickListener(object :
                HappyPlaceItemAdapter.OnAdapterItemClickListener {
                override fun onClickCard(position: Int, model: HappyPlaceModel) {
                    val intent = Intent(this@MainActivity, HappyPlaceDetailsActivity::class.java)
                    intent.putExtra(AppConstants.CURRENT_CARD_ITEM, model)
                    startActivity(intent)
                }
            })

            binding?.ivHappyPlacesList?.layoutManager = LinearLayoutManager(this)
            binding?.ivHappyPlacesList?.adapter = happyPlaceItemAdapter
            binding?.ivHappyPlacesList?.visibility = View.VISIBLE
            binding?.tvNoRecordsFound?.visibility = View.GONE
        } else {
            binding?.ivHappyPlacesList?.visibility = View.GONE
            binding?.tvNoRecordsFound?.visibility = View.VISIBLE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}