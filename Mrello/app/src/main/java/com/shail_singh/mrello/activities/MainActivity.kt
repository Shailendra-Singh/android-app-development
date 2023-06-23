package com.shail_singh.mrello.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivityMainBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloUser
import com.shail_singh.mrello.utils.ActivityResultHandler

class MainActivity : BaseActivity(), ActivityResultHandler.OnActivityResultListener {

    companion object ActivityType {
        const val PROFILE: Int = 1
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var profileActivityResultHandler: ActivityResultHandler
    private lateinit var userName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        drawer = binding.mainDrawer
        setContentView(binding.root)

        profileActivityResultHandler = ActivityResultHandler(this)

        initializeActionBar()
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_my_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    this.profileActivityResultHandler.launchIntent(
                        intent, this, PROFILE
                    )
                }

                R.id.nav_sign_out -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, IntroActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }

        val fabButton = binding.mainActivity.contentMainActivity.fabAddBoard
        fabButton.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.MRELLO_USER_NAME, this.userName)
            startActivity(intent)
        }

        MrelloFirestore().loadUserData(this)
    }

    private fun initializeActionBar() {
        val actionBarView = binding.mainActivity.activityToolbar
        setSupportActionBar(actionBarView)
        actionBarView.setNavigationIcon(R.drawable.ic_menu_icon_open)
        actionBarView.setNavigationOnClickListener {
            toggleDrawer()
        }

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = resources.getString(R.string.app_name)
        }
    }

    private fun toggleDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    fun updateNavigationUserDetails(loggedInUser: MrelloUser) {
        this.userName = loggedInUser.name

        // Update header layout
        val tvUserName = findViewById<TextView>(R.id.tv_user_name)
        val ivUserProfileThumbnail = findViewById<ImageView>(R.id.iv_user_profile_image_thumbnail)

        tvUserName.text = loggedInUser.name

        Glide.with(this).load(loggedInUser.image).centerCrop()
            .placeholder(R.drawable.ic_default_profile_pic).into(ivUserProfileThumbnail)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.doubleBackToExit()
        }
    }

    override fun onActivityResult(customActivityCode: Int, result: ActivityResult?) {
        if (result?.resultCode == RESULT_OK) {
            when (customActivityCode) {
                PROFILE -> {
                    MrelloFirestore().loadUserData(this)
                }
            }
        }
    }
}