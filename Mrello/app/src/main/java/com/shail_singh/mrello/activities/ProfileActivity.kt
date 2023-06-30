package com.shail_singh.mrello.activities

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivityProfileBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloUser
import com.shail_singh.mrello.utils.ImageSelectionHandler
import com.shail_singh.mrello.utils.Utilities


class ProfileActivity : BaseActivity(), ImageSelectionHandler.ImageSelectionListener {

    private lateinit var binding: ActivityProfileBinding

    private var userProfileImageUri: Uri? = null
    private var isCapturedFromCamera: Boolean = false
    private var capturedFromCameraBitmap: Bitmap? = null
    private var updatedUser: MrelloUser? = null
    private var savedUser: MrelloUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.initializeActionBar(
            binding.activityToolbar, resources.getString(R.string.my_profile)
        )

        MrelloFirestore().loadUserData(this, false)
        binding.btnUpdate.setOnClickListener {
            updatedUser = savedUser?.copy()
            updateProfileData()
        }

        val imageSelectionHandler = ImageSelectionHandler(this)
        imageSelectionHandler.setImageSelectionListener(this@ProfileActivity)

        binding.ivUserProfileImage.setOnClickListener {
            imageSelectionHandler.showSelectionDialog()
        }
    }

    fun setUserDataInUI(loggedInUser: MrelloUser) {
        this.savedUser = loggedInUser.copy()
        binding.etName.setText(loggedInUser.name)

        Glide.with(this).load(loggedInUser.image).centerCrop()
            .placeholder(R.drawable.ic_default_profile_pic).into(binding.ivUserProfileImage)

        binding.etEmail.setText(loggedInUser.email)
        if (loggedInUser.mobile != -1L) binding.etMobile.setText(loggedInUser.mobile.toString())
    }

    // endregion
    private fun updateProfileData() {
        super.showProgressDialog(resources.getString(R.string.please_wait))

        val newName = this.binding.etName.text?.toString()
        val newMobile = this.binding.etMobile.text?.toString()

        if (newName.isNullOrEmpty()) {
            super.showErrorSnackBar(resources.getString(R.string.error_empty_name))
            return
        } else {
            this.updatedUser?.name = newName
        }

        if (newMobile?.isNotEmpty()!!) {
            this.updatedUser?.mobile = newMobile.toLong()
        } else {
            this.updatedUser?.mobile = -1
        }

        val fileExtension = if (this.userProfileImageUri != null) {
            Utilities.getFileExtension(this.userProfileImageUri!!)!!.toString()
        } else {
            "jpg"
        }

        val inputBytes: ByteArray? = when {
            this.isCapturedFromCamera -> Utilities.getImageBytes(this.capturedFromCameraBitmap!!)

            this.userProfileImageUri != null -> {
                Utilities.getImageBytes(
                    this, this.userProfileImageUri!!
                )
            }

            else -> null
        }

        if (inputBytes != null) {
            val firebaseStorageImagePath = Utilities.generateFileName(
                Constants.USER_IMAGE_PREFIX, fileExtension
            )

            val sRef: StorageReference =
                FirebaseStorage.getInstance().reference.child(firebaseStorageImagePath)

            sRef.putBytes(inputBytes).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                    // Get the image's firestore url
                    val downloadUrl = it.toString()

                    // update int updated user model
                    this.updatedUser?.image = downloadUrl

                    // persist data in firebase
                    persistToFireStore()
                }
            }.addOnFailureListener {
                super.showErrorSnackBar(resources.getString(R.string.error_image_upload))
            }
        } else {
            persistToFireStore()
        }
    }

    private fun persistToFireStore() {
        val userUpdatesHashMap: HashMap<String, Any> = HashMap()

        if (this.savedUser!!.name != this.updatedUser!!.name) {
            userUpdatesHashMap[Constants.NAME] = this.updatedUser!!.name
        }

        if (this.savedUser!!.mobile != this.updatedUser!!.mobile) {
            userUpdatesHashMap[Constants.MOBILE] = this.updatedUser!!.mobile
        }

        if (this.savedUser!!.image != this.updatedUser!!.image) {
            userUpdatesHashMap[Constants.IMAGE] = this.updatedUser!!.image
        }

        if (userUpdatesHashMap.isEmpty()) {
            setResult(Activity.RESULT_CANCELED)
            MrelloFirestore().updateUserProfileData(this, null)
        } else {
            setResult(Activity.RESULT_OK)
            MrelloFirestore().updateUserProfileData(this, userUpdatesHashMap)
        }
    }

    override fun onPickFromGallery(contentURI: Uri?) {
        this.userProfileImageUri = contentURI!!
        this.isCapturedFromCamera = false
        Glide.with(this).load(contentURI).centerCrop()
            .placeholder(R.drawable.ic_default_profile_pic).into(binding.ivUserProfileImage)
    }

    override fun onCaptureFromCamera(bitmap: Bitmap?) {
        if (bitmap != null) {
            this.capturedFromCameraBitmap = bitmap
            this.isCapturedFromCamera = true
            Glide.with(this).load(bitmap).centerCrop()
                .placeholder(R.drawable.ic_default_profile_pic).into(binding.ivUserProfileImage)
        }
    }
}