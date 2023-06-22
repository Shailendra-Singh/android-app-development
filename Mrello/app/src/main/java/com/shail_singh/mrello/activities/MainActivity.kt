package com.shail_singh.mrello.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.shail_singh.mrello.R
import com.shail_singh.mrello.databinding.ActivityMainBinding
import com.shail_singh.mrello.firebase.MrelloFirestore
import com.shail_singh.mrello.models.MrelloUser

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        drawer = binding.mainDrawer
        setContentView(binding.root)

        initializeActionBar()
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_my_profile -> Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
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

        MrelloFirestore().loginUser(this)
    }

    private fun initializeActionBar() {
        val actionBarView = binding.mainActivity.toolbarMainActivity
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
        // Update header layout
        val tvUserName = findViewById<TextView>(R.id.tv_user_name)
        val ivUserProfileThumbnail = findViewById<ImageView>(R.id.iv_user_profile_image_thumbnail)

        tvUserName.text = loggedInUser.name

        Glide.with(this).load(loggedInUser.image).centerCrop()
            .placeholder(R.drawable.ic_default_profile_pic).into(ivUserProfileThumbnail)
    }

//    override fun onBackPressed() {
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START)
//        } else {
//            super.doubleBackToExit()
//        }
//    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.doubleBackToExit()
        }
        return super.getOnBackInvokedDispatcher()
    }
}