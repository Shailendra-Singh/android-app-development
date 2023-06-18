package com.shail_singh.happyplaces.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shail_singh.happyplaces.AppConstants
import com.shail_singh.happyplaces.adapters.HappyPlaceItemAdapter
import com.shail_singh.happyplaces.database.DatabaseHandler
import com.shail_singh.happyplaces.databinding.ActivityMainBinding
import com.shail_singh.happyplaces.models.HappyPlaceModel
import com.shail_singh.happyplaces.utils.StorageOperations
import com.shail_singh.happyplaces.utils.SwipeToDeleteCallback
import com.shail_singh.happyplaces.utils.SwipeToEditCallback
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var dbHandler: DatabaseHandler
    private lateinit var swipeToEditCallbackHandler: SwipeToEditCallback
    private lateinit var swipeToDeleteCallbackHandler: SwipeToDeleteCallback
    private var happyPlacesList: List<HappyPlaceModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.fabAddHappyPlace?.setOnClickListener {
            startActivity(Intent(this, AddHappyPlaceActivity::class.java))
        }

        this.dbHandler = DatabaseHandler(application)
        lifecycleScope.launch {
            dbHandler.fetchAllPlaces().collect {
                populateHappyPlacesView(it)
            }
        }

        this.swipeToEditCallbackHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        // Do something when a user swept right
                        val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
                        intent.putExtra(
                            AppConstants.CURRENT_CARD_ITEM, happyPlacesList!![position]
                        )
                        startActivity(intent)
                    }
                }
            }
        }

        this.swipeToDeleteCallbackHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = happyPlacesList!![position]
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        // Do something when a user swept left
                        lifecycleScope.launch {
                            dbHandler.deletePlace(item)
                            StorageOperations.deleteFileFromStorage(item.imagePath!!)
                            Toast.makeText(
                                this@MainActivity, "Happy Place DELETED!", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        ItemTouchHelper(this.swipeToEditCallbackHandler).attachToRecyclerView(binding?.ivHappyPlacesList)
        ItemTouchHelper(this.swipeToDeleteCallbackHandler).attachToRecyclerView(binding?.ivHappyPlacesList)
    }

    private fun populateHappyPlacesView(happyPlacesList: List<HappyPlaceModel>) {
        if (happyPlacesList.isNotEmpty()) {
            this.happyPlacesList = happyPlacesList

            val happyPlaceItemAdapter = HappyPlaceItemAdapter(happyPlacesList)
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

    override fun onResume() {
        super.onResume()
        if (happyPlacesList != null) populateHappyPlacesView(happyPlacesList!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}