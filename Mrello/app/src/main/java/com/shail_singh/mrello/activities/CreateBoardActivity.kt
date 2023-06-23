package com.shail_singh.mrello.activities

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivityCreateBoardBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloBoard
import com.shail_singh.mrello.utils.ImageSelectionHandler
import com.shail_singh.mrello.utils.Utilities

class CreateBoardActivity : BaseActivity(), ImageSelectionHandler.ImageSelectionListener {

    private lateinit var binding: ActivityCreateBoardBinding
    private lateinit var userName: String
    private var imageBytes: ByteArray? = null
    private var imageExtension: String = "jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.initializeActionBar(
            binding.activityToolbar, resources.getString(R.string.create_board)
        )

        binding.btnCreate.setOnClickListener {
            createBoard()
        }

        val imageSelectionHandler = ImageSelectionHandler(this)
        imageSelectionHandler.setImageSelectionListener(this@CreateBoardActivity)
        binding.ivBoardImage.setOnClickListener {
            imageSelectionHandler.showSelectionDialog()
        }

        if (intent.hasExtra(Constants.MRELLO_USER_NAME)) {
            this.userName = intent.getStringExtra(Constants.MRELLO_USER_NAME)!!
        }
    }


    private fun createBoard() {
        super.showProgressDialog(resources.getString(R.string.please_wait))

        val name: String = binding.etBoardName.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(name)) {
            super.showErrorSnackBar(resources.getString(R.string.error_empty_name))
        } else {
            // check if there is an image to upload
            if (this.imageBytes != null) {
                val firebaseStorageImagePath = Utilities.generateFileName(
                    Constants.BOARD_IMAGE_PREFIX, this.imageExtension
                )
                val sRef: StorageReference =
                    FirebaseStorage.getInstance().reference.child(firebaseStorageImagePath)
                sRef.putBytes(this.imageBytes!!).addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        // Get the image's firestore url
                        val downloadUrl = it.toString()

                        // Create board's object
                        val board = MrelloBoard(
                            name = name,
                            image = downloadUrl,
                            createdBy = this.userName,
                            createdDate = Utilities.getTodayDateString()
                        )

                        // persist data in firebase
                        persistToFireStore(board)
                    }
                }.addOnFailureListener {
                    super.showErrorSnackBar(resources.getString(R.string.error_image_upload))
                }
            } else {
                val board = MrelloBoard(
                    name = name,
                    createdBy = this.userName,
                    createdDate = Utilities.getTodayDateString()
                )
                persistToFireStore(board)
            }
        }
    }

    private fun persistToFireStore(board: MrelloBoard) {
        MrelloFirestore().createBoard(this, board)
    }

    fun updateBoardDataSuccess() {
        Toast.makeText(this, "CREATED", Toast.LENGTH_LONG).show()
        super.dismissProgressDialog()
        finish()
    }

    override fun onPickFromGallery(contentURI: Uri?) {
        if (contentURI != null) {
            Glide.with(this).load(contentURI).centerCrop()
                .placeholder(R.drawable.ic_space_dashboard_24).into(binding.ivBoardImage)
            this.imageBytes = Utilities.getImageBytes(this, contentURI)
            this.imageExtension = Utilities.getFileExtension(contentURI)!!
        } else {
            super.showErrorSnackBar(resources.getString(R.string.error_gallery_image_select_action))
        }
    }

    override fun onCaptureFromCamera(bitmap: Bitmap?) {
        if (bitmap != null) {
            Glide.with(this).load(bitmap).centerCrop().placeholder(R.drawable.ic_space_dashboard_24)
                .into(binding.ivBoardImage)
            this.imageBytes = Utilities.getImageBytes(bitmap)
        } else {
            super.showErrorSnackBar(resources.getString(R.string.error_camera_image_capture_action))
        }
    }
}