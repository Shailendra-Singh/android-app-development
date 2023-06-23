package com.shail_singh.mrello.activities

import android.os.Bundle
import android.widget.Toast
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivityCreateBoardBinding

class CreateBoardActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeActionBar()

        binding.btnCreate.setOnClickListener {
            Toast.makeText(this, "CREATED", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeActionBar() {
        setSupportActionBar(binding.activityToolbar)
        binding.activityToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = resources.getString(R.string.create_board)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }
}