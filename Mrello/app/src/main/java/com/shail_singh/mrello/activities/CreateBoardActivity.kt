package com.shail_singh.mrello.activities

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivityCreateBoardBinding
import com.shail_singh.mrello.utils.ImageSelectionHandler

class CreateBoardActivity : BaseActivity(), ImageSelectionHandler.ImageSelectionListener {

    private lateinit var binding: ActivityCreateBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.initializeActionBar(
            binding.activityToolbar, resources.getString(R.string.create_board)
        )

        binding.btnCreate.setOnClickListener {
            Toast.makeText(this, "CREATED", Toast.LENGTH_LONG).show()
        }

        val imageSelectionHandler = ImageSelectionHandler(this)
        imageSelectionHandler.setImageSelectionListener(this@CreateBoardActivity)
        binding.ivBoardImage.setOnClickListener {
            imageSelectionHandler.showSelectionDialog()
        }
    }

    override fun onPickFromGallery(contentURI: Uri?) {
        Glide.with(this).load(contentURI).centerCrop().placeholder(R.drawable.ic_space_dashboard_24)
            .into(binding.ivBoardImage)
    }

    override fun onCaptureFromCamera(bitmap: Bitmap?) {
        Glide.with(this).load(bitmap).centerCrop().placeholder(R.drawable.ic_space_dashboard_24)
            .into(binding.ivBoardImage)
    }
}