package com.shail_singh.mrello.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivityProfileBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloUser
import com.shail_singh.mrello.utils.ActivityResultHandler
import com.shail_singh.mrello.utils.PermissionsManager
import java.io.ByteArrayOutputStream
import java.util.UUID


class ProfileActivity : BaseActivity(), ActivityResultHandler.OnActivityResultListener {
    companion object ActivityType {
        const val GALLERY: Int = 1
        const val CAMERA: Int = 2
    }

    private lateinit var binding: ActivityProfileBinding
    private lateinit var galleryResultHandler: ActivityResultHandler
    private lateinit var cameraResultHandler: ActivityResultHandler
    private var userProfileImageUri: Uri? = null
    private var isCapturedFromCamera: Boolean = false
    private var capturedFromCameraBitmap: Bitmap? = null
    private var updatedUser: MrelloUser? = null
    private var savedUser: MrelloUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeActionBar()
        galleryResultHandler = ActivityResultHandler(this@ProfileActivity)
        cameraResultHandler = ActivityResultHandler(this@ProfileActivity)

        MrelloFirestore().loadUserData(this)
        binding.btnUpdate.setOnClickListener {
            updatedUser = savedUser?.copy()
            updateProfileData()
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
        setSupportActionBar(binding.activityToolbar)
        binding.activityToolbar.setNavigationOnClickListener {
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
        this.savedUser = loggedInUser.copy()
        binding.etName.setText(loggedInUser.name)

        Glide.with(this).load(loggedInUser.image).centerCrop()
            .placeholder(R.drawable.ic_default_profile_pic).into(binding.ivUserProfileImage)

        binding.etEmail.setText(loggedInUser.email)
        if (loggedInUser.mobile != -1L) binding.etMobile.setText(loggedInUser.mobile.toString())
    }

    // region Get new profile pic from gallery or camera
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
                        this.userProfileImageUri = contentURI!!
                        this.isCapturedFromCamera = false
                        Glide.with(this).load(contentURI).centerCrop()
                            .placeholder(R.drawable.ic_default_profile_pic)
                            .into(binding.ivUserProfileImage)
                    }

                    CAMERA -> {
                        @Suppress("DEPRECATION") val thumbnail = data.extras?.get("data") as Bitmap
//                        val uri = saveTempImage(thumbnail)
                        this.capturedFromCameraBitmap = thumbnail
                        this.isCapturedFromCamera = true
                        Glide.with(this).load(thumbnail).centerCrop()
                            .placeholder(R.drawable.ic_default_profile_pic)
                            .into(binding.ivUserProfileImage)
                    }
                }
            }
        }
    }

    // endregion
    private fun updateProfileData() {
        super.showProgressDialog(resources.getString(R.string.please_wait))

        val newName = this.binding.etName.text?.toString()
        val newMobile = this.binding.etMobile.text?.toString()

        if (newName.isNullOrEmpty()) {
            Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_LONG).show()
            super.dismissProgressDialog()
            return
        } else {
            this.updatedUser?.name = newName
        }

        if (newMobile?.isNotEmpty()!!) {
            this.updatedUser?.mobile = newMobile.toLong()
        } else {
            this.updatedUser?.mobile = -1
        }

        var inputBytes: ByteArray? = null
        var fileExtension = "jpg"

        if (this.isCapturedFromCamera) {
            val stream = ByteArrayOutputStream()
            this.capturedFromCameraBitmap?.compress(Bitmap.CompressFormat.PNG, 90, stream)
            inputBytes = stream.toByteArray()
            stream.close()
        } else if (this.userProfileImageUri != null) {
            val stream = contentResolver.openInputStream(this.userProfileImageUri!!)!!
            inputBytes = stream.readBytes()
            fileExtension = getFileExtension(this.userProfileImageUri!!)!!.toString()
            stream.close()
        }

        if (inputBytes != null) {
            val firebaseStorageImagePath = "USER_IMAGE_${UUID.randomUUID()}.${fileExtension}"
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
                Toast.makeText(this, "Couldn't upload image to server!", Toast.LENGTH_LONG).show()
            }
        } else {
            persistToFireStore()
        }
    }

    private fun persistToFireStore() {
        val userUpdatesHashMap: HashMap<String, Any> = HashMap()

        if (this.savedUser!!.name != this.updatedUser!!.name) {
            userUpdatesHashMap[Constants.MRELLO_USER_NAME] = this.updatedUser!!.name
        }

        if (this.savedUser!!.mobile != this.updatedUser!!.mobile) {
            userUpdatesHashMap[Constants.MRELLO_USER_MOBILE] = this.updatedUser!!.mobile
        }

        if (this.savedUser!!.image != this.updatedUser!!.image) {
            userUpdatesHashMap[Constants.MRELLO_USER_IMAGE] = this.updatedUser!!.image
        }

        if (userUpdatesHashMap.isEmpty()) {
            setResult(Activity.RESULT_CANCELED)
            MrelloFirestore().updateUserProfileData(this, null)
        } else {
            setResult(Activity.RESULT_OK)
            MrelloFirestore().updateUserProfileData(this, userUpdatesHashMap)
        }
    }

    fun updateProfileDataSuccess() {
        super.dismissProgressDialog()
        finish()
    }

    private fun getFileExtension(uri: Uri?): String? {
        val uriString: String = uri?.toString()!!
        return MimeTypeMap.getFileExtensionFromUrl(uriString)
    }
}