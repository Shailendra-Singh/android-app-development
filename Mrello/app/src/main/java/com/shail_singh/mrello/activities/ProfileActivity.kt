package com.shail_singh.mrello.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import com.bumptech.glide.Glide
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivityProfileBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloUser
import com.shail_singh.mrello.utils.ActivityResultHandler
import com.shail_singh.mrello.utils.PermissionsManager


class ProfileActivity : BaseActivity(), ActivityResultHandler.OnActivityResultListener {
    companion object ActivityType {
        const val GALLERY: Int = 1
        const val CAMERA: Int = 2
    }

    private lateinit var binding: ActivityProfileBinding
    private lateinit var galleryResultHandler: ActivityResultHandler
    private lateinit var cameraResultHandler: ActivityResultHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeActionBar()
        galleryResultHandler = ActivityResultHandler(this@ProfileActivity)
        cameraResultHandler = ActivityResultHandler(this@ProfileActivity)

        MrelloFirestore().loadUserData(this)
        binding.btnUpdate.setOnClickListener {

        }

        binding.ivUserProfileImage.setOnClickListener {
            try {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select action")
                val pictureDialogItems = arrayOf(
                    resources.getString(R.string.action_select_photo_gallery),
                    resources.getString(R.string.action_capture_photo_camera)
                )
                pictureDialog.setItems(pictureDialogItems) { _, which ->
                    when (which) {
                        0 -> selectProfilePicFromGallery()
                        1 -> captureProfilePicFromCamera()
                    }
                }
                pictureDialog.show()
            } catch (e: Exception) {
                Toast.makeText(
                    this, resources.getString(R.string.error_profile_image_setup), Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun initializeActionBar() {
        setSupportActionBar(binding.tbProfileToolbar)
        binding.tbProfileToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = resources.getString(R.string.nav_text_my_profile)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    fun setUserDataInUI(loggedInUser: MrelloUser) {
        binding.etName.setText(loggedInUser.name)

        Glide.with(this).load(loggedInUser.image).centerCrop()
            .placeholder(R.drawable.ic_default_profile_pic).into(binding.ivUserProfileImage)

        binding.etEmail.setText(loggedInUser.email)
        if (loggedInUser.mobile != -1L) binding.etMobile.setText(loggedInUser.mobile.toString())
    }

    private fun selectProfilePicFromGallery() {
        val galleryPermissionManager = PermissionsManager(
            this, GALLERY.toString(), mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )

        galleryPermissionManager.requestPermissions(object :
            PermissionsManager.IPermissionsCheckedListener {
            override fun onPermissionsChecked() {
                val selectPhotoFromGalleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryResultHandler.launchIntent(
                    selectPhotoFromGalleryIntent, this@ProfileActivity, GALLERY
                )
            }
        })
    }

    private fun captureProfilePicFromCamera() {
        val cameraPermissionsManager = PermissionsManager(
            this, CAMERA.toString(), mutableListOf(
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            )
        )

        cameraPermissionsManager.requestPermissions(object :
            PermissionsManager.IPermissionsCheckedListener {
            override fun onPermissionsChecked() {
                val takePhotoFromCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraResultHandler.launchIntent(
                    takePhotoFromCameraIntent, this@ProfileActivity, CAMERA
                )
            }
        })
    }

    override fun onActivityResult(customActivityCode: Int, result: ActivityResult?) {
        if (result?.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                when (customActivityCode) {
                    GALLERY -> {
                        val contentURI = data.data
                        Glide.with(this).load(contentURI).centerCrop()
                            .placeholder(R.drawable.ic_default_profile_pic)
                            .into(binding.ivUserProfileImage)
                    }

                    CAMERA -> {
                        @Suppress("DEPRECATION") val thumbnail = data.extras?.get("data") as Bitmap
                        Glide.with(this).load(thumbnail).centerCrop()
                            .placeholder(R.drawable.ic_default_profile_pic)
                            .into(binding.ivUserProfileImage)
                    }
                }
            }
        }
    }
}