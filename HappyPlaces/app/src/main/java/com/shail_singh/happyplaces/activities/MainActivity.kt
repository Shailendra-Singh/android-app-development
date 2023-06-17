package com.shail_singh.happyplaces.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.shail_singh.happyplaces.database.DatabaseHandler
import com.shail_singh.happyplaces.databinding.ActivityMainBinding
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
                binding?.tvPlaceCount?.text = "There are ${it.size} places saved."
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}