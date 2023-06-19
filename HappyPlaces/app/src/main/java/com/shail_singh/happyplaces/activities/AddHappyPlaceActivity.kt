package com.shail_singh.happyplaces.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.shail_singh.happyplaces.AppConstants
import com.shail_singh.happyplaces.R
import com.shail_singh.happyplaces.database.DatabaseHandler
import com.shail_singh.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.shail_singh.happyplaces.models.HappyPlaceModel
import com.shail_singh.happyplaces.utils.MapsApiOperations
import com.shail_singh.happyplaces.utils.StorageOperations
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    private var binding: ActivityAddHappyPlaceBinding? = null
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var galleryActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var placeApiActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var dbHandler: DatabaseHandler

    // Place data variables
    private var title: String = ""
    private var description: String = ""
    private var dateStr: String = ""
    private var location: String = ""
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var placeBitmap: Bitmap? = null

    // Variables for Edit Activity
    private var happyPlaceEditModel: HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.tbAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.tbAddPlace?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (!Places.isInitialized()) {
            val mapsApiKey =
                applicationContext.applicationInfo.metaData.getString("com.google.android.geo.API_KEY")
            Places.initialize(this@AddHappyPlaceActivity, mapsApiKey!!)
        }

        dbHandler = DatabaseHandler(application)

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        updateDateInView()

        galleryActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                actionOnGalleryActivityResult(it)
            }

        cameraActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                actionOnCameraActivityResult(it)
            }

        placeApiActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                actionOnPlaceApiActivityResult(it)
            }

        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImageBtn?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)
        binding?.etLocation?.setOnClickListener(this)
        binding?.tvUseCurrentLocation?.setOnClickListener(this)

        // Set variables for Edit action
        if (intent.hasExtra(AppConstants.CURRENT_CARD_ITEM)) {
            this.happyPlaceEditModel = intent.parcelable(AppConstants.CURRENT_CARD_ITEM)
            preFillForEdit(this.happyPlaceEditModel)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            R.id.tv_add_image_btn -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select Photo from Gallery", "Capture photo from Camera")
                pictureDialog.setItems(pictureDialogItems) { _, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()

                        1 -> capturePhotoFromCamera()
                    }
                }

                pictureDialog.show()
            }

            R.id.btn_save -> {

                val isEditAction = binding?.btnSave?.text == getString(R.string.btn_text_update)

                when {
                    binding?.etTitle?.text.isNullOrEmpty() -> showToast("Title can't be empty!")
                    binding?.etDescription?.text.isNullOrEmpty() -> showToast("Description can't be empty!")
                    binding?.etLocation?.text.isNullOrEmpty() -> showToast("Location not provided")
                    this.placeBitmap == null -> showToast("Please select an image!")
                    else -> {
                        this.title = binding?.etTitle?.text.toString()
                        this.description = binding?.etDescription?.text.toString()
                        this.dateStr = binding?.etDate?.text.toString()
                        this.location = binding?.etLocation?.text.toString()
                        if (isEditAction) onActionHappyPlaceUpdate()
                        else onActionHappyPlaceSave()
                    }
                }
            }

            R.id.et_location -> {
                try {
                    val fields = listOf(
                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS
                    )
                    val intent =
                        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(this@AddHappyPlaceActivity)
                    placeApiActivityResultLauncher.launch(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            R.id.tv_use_current_location -> {
                if (!isLocationAvailable()) {
                    showToast("Your Location provider is turned off. Please turn it on.")
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                } else {
                    getCurrentLocation()
                }
            }
        }
    }

    private fun isLocationAvailable(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestCurrentLocation() {

        val fusedLocationProviderClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(1000).apply {
            setMaxUpdates(1)
            setPriority(LocationRequest.QUALITY_HIGH_ACCURACY)
        }.build()

        val locationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation: Location = locationResult.lastLocation!!
                this@AddHappyPlaceActivity.lat = lastLocation.latitude
                this@AddHappyPlaceActivity.lon = lastLocation.longitude
                Log.i("LOC", "Location: [${lastLocation.latitude}, ${lastLocation.longitude}]")
                val mapsAddressHandler = MapsApiOperations.CoordinateAddressHandler(
                    this@AddHappyPlaceActivity,
                    this@AddHappyPlaceActivity.lat,
                    this@AddHappyPlaceActivity.lon
                )

                lifecycleScope.launch {
                    val addressStr = mapsAddressHandler.getAddress()
                    if (!addressStr.isNullOrEmpty()) {
                        showToast("Current Location Obtained!")
                        this@AddHappyPlaceActivity.binding?.etLocation?.setText(addressStr)
                        this@AddHappyPlaceActivity.location = addressStr
                    } else {
                        showToast("Couldn't get current address!")
                    }
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }

    private fun getCurrentLocation() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        ).withListener(object : MultiplePermissionsListener {
            @RequiresApi(Build.VERSION_CODES.S)
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    requestCurrentLocation()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?, token: PermissionToken?
            ) {
                showRationalDialogForPermissions("LOCATION")
            }
        }).onSameThread().check()
    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryActivityResultLauncher.launch(galleryIntent)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?, token: PermissionToken?
            ) {
                showRationalDialogForPermissions("READ/WRITE")
            }

        }).onSameThread().check()
    }

    // Gallery Activity
    private fun actionOnGalleryActivityResult(result: ActivityResult?) {
        if (result?.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                val contentURI = data.data
                try {
                    val inputStream = contentResolver.openInputStream(contentURI!!)
                    val thumbnail = BitmapFactory.decodeStream(inputStream)
                    this.placeBitmap = thumbnail
                    inputStream!!.close()
                    binding?.ivPlaceImage?.setImageBitmap(this.placeBitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    showToast("Couldn't select image!")
                }
            }
        }
    }

    private fun capturePhotoFromCamera() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraActivityResultLauncher.launch(cameraIntent)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?, token: PermissionToken?
            ) {
                showRationalDialogForPermissions("CAMERA")
            }

        }).onSameThread().check()
    }

    // Camera Activity
    private fun actionOnCameraActivityResult(result: ActivityResult?) {
        if (result?.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                @Suppress("DEPRECATION") val thumbnail = data.extras?.get("data") as Bitmap
                try {
                    this.placeBitmap = thumbnail
                    binding?.ivPlaceImage?.setImageBitmap(this.placeBitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    showToast("Couldn't select image!")
                }
            }
        }
    }

    private fun showRationalDialogForPermissions(permissionName: String) {
        AlertDialog.Builder(this@AddHappyPlaceActivity).setMessage(
            "Unable to obtain $permissionName permissions.\n Grant required permissions from Settings"
        ).setPositiveButton("GO TO SETTINGS") { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
        }.show()
    }

    // Place API Activity
    private fun actionOnPlaceApiActivityResult(result: ActivityResult?) {
        if (result?.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                val place: Place = Autocomplete.getPlaceFromIntent(data)
                this.location = place.address!!
                binding?.etLocation?.setText(this.location)
                this.lat = place.latLng?.latitude ?: 0.0
                this.lon = place.latLng?.longitude ?: 0.0
            }
        }
    }

    private fun updateDateInView() {
        val sdf = SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.getDefault())
        binding?.etDate?.setText(sdf.format(cal.time).toString())
    }

    private fun saveBitmapToStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        val directory = wrapper.getDir(AppConstants.IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        val file = File(directory, "${UUID.randomUUID()}.jpg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }

    private fun onActionHappyPlaceSave() {

        // Persist selected image
        val placeImgUri = saveBitmapToStorage(this.placeBitmap!!)

        // Create model
        val place = HappyPlaceModel(
            title = this.title,
            description = this.description,
            imagePath = placeImgUri.toString(),
            date = this.dateStr,
            latitude = this.lat,
            longitude = this.lon,
            location = this.location
        )

        // Persist data to storage
        lifecycleScope.launch {
            dbHandler.insertPlace(place)
            showToast("Happy Place Saved")
        }

        // Close activity
        finish()
    }

    private fun onActionHappyPlaceUpdate() {
        // Persist selected image
        val placeImgUri = saveBitmapToStorage(this.placeBitmap!!)

        // If new image is different from previously stored, then delete old image
        StorageOperations.deleteFileFromStorage(this.happyPlaceEditModel?.imagePath!!)

        // Create model
        val place = HappyPlaceModel(
            id = this.happyPlaceEditModel?.id!!,
            title = this.title,
            description = this.description,
            imagePath = placeImgUri.toString(),
            date = this.dateStr,
            latitude = this.lat,
            longitude = this.lon,
            location = this.location
        )

        // Persist data to storage
        lifecycleScope.launch {
            dbHandler.updatePlace(place)
            showToast("Happy Place UPDATED")
        }

        // Close activity
        finish()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@AddHappyPlaceActivity, msg, Toast.LENGTH_LONG).show()
    }

    private fun preFillForEdit(item: HappyPlaceModel?) {
        if (item != null) {
            val savedImageUri = Uri.parse(item.imagePath)

            binding?.etTitle?.setText(item.title)
            binding?.etDescription?.setText(item.description)
            binding?.etDate?.setText(item.date)
            binding?.etLocation?.setText(item.location)
            binding?.ivPlaceImage?.setImageURI(savedImageUri)
            binding?.btnSave?.text = getString(R.string.btn_text_update)

            readPlaceImageFromStorage(savedImageUri!!)
            supportActionBar?.title = "UPDATE HAPPY PLACE"
        }
    }

    private fun readPlaceImageFromStorage(uri: Uri) {
        val bitmap = StorageOperations.readBitmapFromStorage(uri)
        if (bitmap != null) {
            this.placeBitmap = bitmap
            binding?.ivPlaceImage?.setImageBitmap(this.placeBitmap)
        } else {
            showToast("Couldn't read image!")
            this.placeBitmap = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}