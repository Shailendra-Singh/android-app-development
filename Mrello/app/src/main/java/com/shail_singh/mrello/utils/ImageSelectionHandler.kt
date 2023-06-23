package com.shail_singh.mrello.utils

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import com.shail_singh.mrello.R
import com.shail_singh.mrello.activities.BaseActivity

class ImageSelectionHandler(private val activity: BaseActivity) {
    companion object Mode {
        const val GALLERY: Int = 1
        const val CAMERA: Int = 2
    }

    interface ImageSelectionListener {
        fun onPickFromGallery(contentURI: Uri?)
        fun onCaptureFromCamera(bitmap: Bitmap?)
    }

    private val galleryResultHandler: ActivityResultHandler = ActivityResultHandler(this.activity)
    private val cameraResultHandler: ActivityResultHandler = ActivityResultHandler(this.activity)
    private val imageSelectionActivityHandler: ImageSelectionActivityResultsHandler =
        ImageSelectionActivityResultsHandler()
    private lateinit var imageSelectionListener: ImageSelectionListener

    fun setImageSelectionListener(imageSelectionListener: ImageSelectionListener) {
        this.imageSelectionListener = imageSelectionListener
    }

    fun showSelectionDialog() {
        val actionDialog = AlertDialog.Builder(activity)
        actionDialog.setTitle("Select action")
        val pictureDialogItems = arrayOf(
            activity.resources.getString(R.string.action_select_photo_gallery),
            activity.resources.getString(R.string.action_capture_photo_camera)
        )
        actionDialog.setItems(pictureDialogItems) { _, which ->
            when (which) {
                0 -> selectProfilePicFromGallery()
                1 -> captureProfilePicFromCamera()
            }
        }
        actionDialog.show()
    }

    // region Get new profile pic from gallery or camera
    private fun selectProfilePicFromGallery() {
        val galleryPermissionManager = PermissionsManager(
            activity, GALLERY.toString(), mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )

        galleryPermissionManager.requestPermissions(object :
            PermissionsManager.IPermissionsCheckedListener {
            override fun onPermissionsChecked() {
                val selectPhotoFromGalleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryResultHandler.launchIntent(
                    selectPhotoFromGalleryIntent, imageSelectionActivityHandler, GALLERY
                )
            }
        })
    }

    private fun captureProfilePicFromCamera() {
        val cameraPermissionsManager = PermissionsManager(
            activity, CAMERA.toString(), mutableListOf(
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            )
        )

        cameraPermissionsManager.requestPermissions(object :
            PermissionsManager.IPermissionsCheckedListener {
            override fun onPermissionsChecked() {
                val takePhotoFromCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraResultHandler.launchIntent(
                    takePhotoFromCameraIntent, imageSelectionActivityHandler, CAMERA
                )
            }
        })
    }

    @Suppress("DEPRECATION")
    inner class ImageSelectionActivityResultsHandler :
        ActivityResultHandler.OnActivityResultListener {
        override fun onActivityResult(customActivityCode: Int, result: ActivityResult?) {
            if (result?.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    when (customActivityCode) {
                        GALLERY -> {
                            val contentURI = data.data
                            this@ImageSelectionHandler.imageSelectionListener.onPickFromGallery(
                                contentURI
                            )
                        }

                        CAMERA -> {
                            val bitmap: Bitmap?
                            try {
                                bitmap = data.extras?.get("data") as Bitmap
                                this@ImageSelectionHandler.imageSelectionListener.onCaptureFromCamera(
                                    bitmap
                                )
                            } catch (e: Exception) {
                                Log.e(this@ImageSelectionHandler.javaClass.simpleName, e.toString())
                            }
                        }
                    }
                }
            }
        }
    }
    // endregion
}